package com.loopon.routine.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.s3.S3Service;
import com.loopon.journey.application.JourneyCommandServiceImpl;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyFeedback;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.infrastructure.JourneyFeedbackJpaRepository;
import com.loopon.journey.domain.ProgressStatus;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.routine.application.dto.converter.RoutineConverter;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.RoutineReport;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.routine.infrastructure.RoutineReportJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final UserRepository userRepository;
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;
    private final S3Service s3Service;
    private final JourneyFeedbackJpaRepository journeyFeedbackRepository;
    private final RoutineReportJpaRepository routineReportRepository;
    private final JourneyCommandService journeyCommandService;

    @Override
    public RoutineResponse.PostRoutinesDto postRoutine(
            Long userId,
            RoutineRequest.CreateJourneyWithRoutineDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (journeyRepository.existsByUserAndStatus(user, JourneyStatus.IN_PROGRESS)) {
            throw new BusinessException(ErrorCode.JOURNEY_IN_PROGRESS);
        }

        Integer maxOrder = journeyRepository.findMaxJourneyOrderByUser(user);
        int nextOrder = (maxOrder == null ? 0 : maxOrder) + 1;

        Journey journey = Journey.builder()
                .user(user)
                .goal(request.goal())
                .category(request.category())
                .status(JourneyStatus.IN_PROGRESS)
                .journeyOrder(nextOrder)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2)) // 3일간
                .build();

        journeyRepository.save(journey);

        //journey Feedback 테이블 생성
        JourneyFeedback journeyFeedback = JourneyFeedback.builder()
                .journey(journey)
                .build();

        journeyFeedbackRepository.save(journeyFeedback);

        List<Routine> routines = request.routines().stream()
                .map(dto -> Routine.builder()
                        .journey(journey)
                        .content(dto.content())
                        .notificationTime(dto.time())
                        .build())
                .collect(Collectors.toList());

        routineRepository.saveAll(routines);

        List<RoutineProgress> progressList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int day = 0; day < 3; day++) {
            LocalDate targetDate = today.plusDays(day);

            for (Routine routine : routines) {
                RoutineProgress progress = RoutineProgress.create(routine, targetDate);

                progressList.add(progress);
            }
        }

        routineProgressRepository.saveAll(progressList);

        return new RoutineResponse.PostRoutinesDto(journey.getId());
    }

    @Override
    public RoutineResponse.RoutineCertifyDto certifyRoutine(Long progressId, Long userId, MultipartFile image) {
        RoutineProgress progress = routineProgressRepository.findById(progressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!progress.getRoutine().getJourney().getUser().getId().equals(userId)) {
            throw new  BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        progress.validateCertifiable();
        String imageUrl = s3Service.uploadFile(image);
        progress.certify(imageUrl);

        //여정 피드백 업데이트
        Long journeyId = progress.getRoutine().getJourney().getId();
        LocalDate targetDay = progress.getProgressDate();
        journeyCommandService.UpdateJourneyFeedback(journeyId, userId, targetDay);

        return RoutineConverter.toRoutineCertifyDto(progress);
    }

    // 루틴 미룬 사유 수정
    @Transactional
    @Override
    public RoutineResponse.RoutinePostponeReasonEditDto editPostponeReason(
            Long progressId,
            Long userId,
            RoutineRequest.editReasonDto newReason
    ) {

        // progress 조회
        RoutineProgress progress = routineProgressRepository.findById(progressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        // 본인 루틴인지 검증
        if (!progress.getRoutine()
                .getJourney()
                .getUser()
                .getId()
                .equals(userId)) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        // 프로그레스 상태 확인
        if (progress.getStatus() != ProgressStatus.POSTPONED) {
            throw new BusinessException(ErrorCode.ROUTINE_NOT_POSTPONABLE);
        }

        // 미룬 사유 수정
        progress.updatePostponeReason(newReason.reason());

        return new RoutineResponse.RoutinePostponeReasonEditDto(
                progress.getId(),
                progress.getPostponedReason()
        );
    }

    //하루 루프 완료 후 리포트 생성
    @Transactional
    @Override
    public RoutineResponse.RoutineReportCreateDto postRoutineReport(
            Long journeyId,
            Long userId,
            RoutineRequest.postRoutineReport request
    ) {

        LocalDate today = LocalDate.now();

        //유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Journey 조회
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 여정입니다."));

        if (!journey.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 유저의 여정이 아닙니다.");
        }

        //오늘 날짜의 routine Progress 조회
        List<RoutineProgress> todayProgresses =
                routineProgressRepository.findByRoutine_Journey_IdAndProgressDate(
                        journeyId, today);


        //IN_PROGRESS 존재 여부 확인
        boolean hasInProgress = todayProgresses.stream()
                .anyMatch(p -> p.getStatus() == ProgressStatus.IN_PROGRESS);

        if (hasInProgress) {
            throw new IllegalStateException("아직 완료되지 않은 루틴이 있습니다.");
        }

        // 리포트 생성
        RoutineReport report = RoutineReport.builder()
                .journey(journey)
                .user(user)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        routineReportRepository.save(report);

        // 여정 완료 여부 확인하기 -> 확인 후 feedback 생성
        completeJourneyIfFinished(journey, userId);

        return new RoutineResponse.RoutineReportCreateDto(
                report.getId(),
                report.getContent()
        );
    }

    public void completeJourneyIfFinished(Journey journey, Long userId) {

        // 모든 progress가 COMPLETED인지 확인
        boolean allCompleted = routineProgressRepository
                .existsByRoutine_Journey_IdAndStatusNot(
                        journey.getId(),
                        ProgressStatus.IN_PROGRESS
                );

        // statusNot이 존재하면 아직 미완료 있음 -> 전체 완료 되었고 endDate가 오늘이면 journey complete()후 feedback 생성
        if (!allCompleted && journey.getEndDate().equals(LocalDate.now())) {
            journey.complete();
            journeyCommandService.UpdateJourneyFeedback(journey.getId(),userId,LocalDate.now());
        }
    }
}
