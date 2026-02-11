package com.loopon.journey.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.JourneyCategory;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.domain.ProgressStatus;
import com.loopon.journey.domain.service.JourneyQueryService;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //진행중인 여정 조회
        Journey journey = journeyRepository
                .findByUserAndStatus(user, JourneyStatus.IN_PROGRESS)
                .orElseThrow(() -> new IllegalStateException("진행 중인 여정이 없습니다."));

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
}
