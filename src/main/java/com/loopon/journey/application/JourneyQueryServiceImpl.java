package com.loopon.journey.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.application.dto.converter.JourneyConverter;
import com.loopon.journey.domain.*;
import com.loopon.journey.infrastructure.JourneyFeedbackJpaRepository;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.RoutineReport;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.routine.infrastructure.RoutineReportJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JourneyQueryServiceImpl implements JourneyQueryService {

    private final UserJpaRepository userRepository;
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;
    private final JourneyFeedbackJpaRepository journeyFeedbackRepository;
    private final RoutineReportJpaRepository routineReportRepository;

    @Override
    public JourneyResponse.JourneyOrderDto getNextJourneyOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Integer maxOrder = journeyRepository.findMaxJourneyOrderByUser(user);

        return new JourneyResponse.JourneyOrderDto(maxOrder + 1);
    }

    @Override
    public JourneyResponse.CurrentJourneyDto getCurrentJourney(Long userId) {

        //유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //진행중인 여정 조회
        Journey journey = journeyRepository
                .findByUserAndStatus(user, JourneyStatus.IN_PROGRESS)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_NOT_FOUND));

        //여정에 속한 루틴 전체 조회
        List<Routine> routines = routineRepository.findAllByJourney(journey);

        //오늘 날짜 기준 값 설정후 오늘의 여정 조회
        LocalDate today = LocalDate.now();

        //오늘 날짜 기준 이전에 완료 되지 않은 여정이 있는지 확인
        Optional<RoutineProgress> notCompletedProgressOpt =
                routineProgressRepository
                        .findFirstByRoutineInAndProgressDateBeforeAndStatusOrderByProgressDateAsc(
                                routines,
                                today,
                                ProgressStatus.IN_PROGRESS
                        );

        boolean isNotReady = notCompletedProgressOpt.isPresent();

        // 조회할 날짜 결정
        LocalDate targetDate =
                notCompletedProgressOpt
                        .map(RoutineProgress::getProgressDate)
                        .orElse(today);

        //target Date에 해당되는 루틴들 조회
        List<RoutineProgress> progresses =
                routineProgressRepository.findAllByRoutineInAndProgressDate(routines, targetDate);

        // routineId → progress 매핑
        Map<Long, RoutineProgress> progressMap =
                progresses.stream()
                        .collect(Collectors.toMap(
                                rp -> rp.getRoutine().getId(),
                                rp -> rp
                        ));

        // 완료 개수 계산
        long completedCount = progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();

        // jourey 일수 계산
        long days =
                ChronoUnit.DAYS.between(journey.getStartDate(), LocalDate.now());

        int journeyDay = (int) days + 1;

        //루틴 dto로 변환
        List<JourneyResponse.RoutineDto> routineDtos =
                routines.stream()
                        .map(routine -> {
                            RoutineProgress progress = progressMap.get(routine.getId());

                            Long progressId = progress != null ? progress.getId() : null;

                            ProgressStatus status =
                                    progress != null ? progress.getStatus() : ProgressStatus.IN_PROGRESS;

                            return new JourneyResponse.RoutineDto(
                                    routine.getId(),
                                    progressId,
                                    routine.getContent(),
                                    routine.getNotificationTime(),
                                    status
                            );
                        })
                        .toList();

        //결과갑 조합 후 Response 생성
        return new JourneyResponse.CurrentJourneyDto(
                new JourneyResponse.JourneyInfoDto(
                        journey.getId(),
                        journey.getJourneyOrder(),
                        journeyDay,
                        journey.getCategory(),
                        journey.getGoal()
                ),
                new JourneyResponse.TodayProgressDto(
                        (int) completedCount,
                        routines.size()
                ),
                routineDtos,
                isNotReady,
                targetDate
        );
    }

    @Override
    public Slice<JourneyResponse.JourneyPreviewDto> searchJourney(
            Long userId,
            String keyword,
            List<Boolean> categories,
            Pageable pageable
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 카테고리 분류
        List<JourneyCategory> journeyCategories = new ArrayList<>();
        JourneyCategory[] temp = JourneyCategory.values();

        for (int i=0; i<3; i++) {
            if (categories.get(i) == true) {
                journeyCategories.add(temp[i]);
            }
        }

        if (journeyCategories.isEmpty()) {
            journeyCategories.add(temp[0]);
            journeyCategories.add(temp[1]);
            journeyCategories.add(temp[2]);
        }

        Slice<Journey> journeys = journeyRepository.findDistinctJourneyBySearch(
                keyword,
                journeyCategories,
                user.getId(),
                pageable
        );

        return journeys.map(journey -> new JourneyResponse.JourneyPreviewDto(
                journey.getId(), journey.getGoal(), journey.getCategory(), journey.getJourneyOrder()
        ));

    }

    @Override
    public Slice<JourneyResponse.JourneyPreviewDto> getJourneyList(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Slice<Journey> journeys = journeyRepository.findDistinctJourneyByUserId(user.getId(), pageable);

        return journeys.map(journey -> new JourneyResponse.JourneyPreviewDto(
                journey.getId(), journey.getGoal(), journey.getCategory(), journey.getJourneyOrder()
        ));
    }

    @Transactional
    @Override
    public List<JourneyResponse.MonthlyCompletedDto> getMonthlyCompleted(
            Long userId,
            int year,
            int month
    ) {

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Object[]> results =
                routineProgressRepository.findCompletedCountByUserAndMonth(
                        userId,
                        startDate,
                        endDate
                );

        //각 날짜 별로 mapping -> 0개인 날도 추가해서 반환.
        Map<LocalDate, Long> completedMap = results.stream()
                .collect(Collectors.toMap(
                        r -> (LocalDate) r[0],
                        r -> (Long) r[1]
                ));

        // 전체 날짜 생성-> 후 매핑
        List<JourneyResponse.MonthlyCompletedDto> response = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            Long count = completedMap.getOrDefault(current, 0L);

            response.add(
                    JourneyResponse.MonthlyCompletedDto.builder()
                            .date(current)
                            .completedCount(count)
                            .build()
            );

            current = current.plusDays(1);
        }

        return response;
    }

    @Transactional
    @Override
    public JourneyResponse.DailyJourneyReportDto getDailyJourneyReport(
            Long userId,
            LocalDate date
    ) {

        //날짜 여정 찾기
        Journey journey = journeyRepository
                .findActiveJourneyByUserAndDate(userId, date)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_NOT_FOUND));

        Long journeyId = journey.getId();

        // 날짜 progress 전체 조회
        List<RoutineProgress> progresses =
                routineProgressRepository.findByJourneyAndDate(journeyId, date);

        //루틴 dto 생성
        List<JourneyResponse.DailyRoutineDto> routineDtos =
                progresses.stream()
                        .map(rp -> new JourneyResponse.DailyRoutineDto(
                                rp.getRoutine().getId(),
                                rp.getRoutine().getContent(),
                                rp.getStatus().name()
                        ))
                        .toList();

        // 완료 개수 찾기
        Long completedCount = progresses.stream()
                .filter(rp -> rp.getStatus() == ProgressStatus.COMPLETED)
                .count();

        // JourneyFeedback 조회
        JourneyFeedback feedback = journeyFeedbackRepository
                .findByJourneyId(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_FEEDBACK_NOT_FOUND));

        // RoutineReport 조회
        Optional<RoutineReport> routineReport =
                routineReportRepository.findByUserAndJourneyAndDate(
                        userId,
                        journeyId,
                        date
                );

        return new JourneyResponse.DailyJourneyReportDto(
                journeyId,
                journey.getGoal(),

                feedback.getDay1Rate(),
                feedback.getDay2Rate(),
                feedback.getDay3Rate(),
                feedback.getTotalRate(),

                completedCount,

                routineReport.map(RoutineReport::getContent),

                routineDtos
        );
    }

    @Transactional
    @Override
    public JourneyResponse.JourneyRecordDto getJourneyRecord(
            Long journeyId,
            Long userId
    ){
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_NOT_FOUND));

        if (!journey.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.JOURNEY_FORBIDDEN);
        }

        boolean hasInProgress = routineProgressRepository
                .existsInProgress(journeyId, ProgressStatus.IN_PROGRESS);

        if (hasInProgress) {
            throw new BusinessException(ErrorCode.ROUTINE_IN_PROGRESS);
        }

        journey.complete();

        //이미 존재하는 feedback 가져오기
        JourneyFeedback feedback = journeyFeedbackRepository
                .findByJourneyId(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_FEEDBACK_NOT_FOUND));

        List<Routine> routines = routineRepository.findByJourney_Id(journeyId);

        return JourneyConverter.toCompleteJourneyDto(journey, feedback, routines);
    }
}
