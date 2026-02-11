package com.loopon.routine.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.s3.S3Service;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.routine.application.dto.converter.RoutineConverter;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final UserRepository userRepository;
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
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

    @Transactional
    @Override
    public RoutineResponse.RoutineCertifyDto certifyRoutine(Long progressId, Long userId, MultipartFile image) {
        RoutineProgress progress = routineProgressRepository.findById(progressId)
                .orElseThrow(() -> new IllegalArgumentException("진행 정보를 찾을 수 없습니다."));

        if (!progress.getRoutine().getJourney().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자의 루틴이 아닙니다.");
        }

        progress.validateCertifiable();
        String imageUrl = s3Service.uploadFile(image);
        progress.certify(imageUrl);

        return RoutineConverter.toRoutineCertifyDto(progress);
    }
}
