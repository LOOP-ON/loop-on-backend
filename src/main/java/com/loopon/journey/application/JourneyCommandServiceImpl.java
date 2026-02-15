package com.loopon.journey.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.JourneyFeedback;
import com.loopon.journey.infrastructure.JourneyFeedbackJpaRepository;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.domain.ProgressStatus;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JourneyCommandServiceImpl implements JourneyCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;
    private final JourneyFeedbackJpaRepository journeyFeedbackRepository;
    private final RoutineJpaRepository routineRepository;

    @Transactional
    @Override
    public JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command) {

        //여정 조회 후 진행중인지 판단.
        Journey journey = journeyRepository.getById(command.journeyId());

        if (journey.getStatus() != JourneyStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.JOURNEY_NOT_IN_PROGRESS);
        }

        //요청한 progress들 중에 IN_PROGRESS인 경우만 조회
        List<RoutineProgress> progresses =
                routineProgressRepository
                        .findAllByIdInAndStatus(
                                command.routineProgressIds(),
                                ProgressStatus.IN_PROGRESS
                        );

        if (progresses.isEmpty()) {
            throw new BusinessException(ErrorCode.ROUTINE_NOT_POSTPONABLE);
        }

        //각 프로그레스의 postpone 이유 입력
        progresses.forEach(progress ->
                progress.postpone(command.reason())
        );

        //피드백 업데이트
        UpdateJourneyFeedback(journey.getId(), command.userId(), progresses.getFirst().getProgressDate());

        return new JourneyResponse.PostponeRoutineDto(
                progresses.stream()
                        .map(RoutineProgress::getId)
                        .distinct()
                        .toList(),
                command.reason()
        );
    }

    @Transactional
    @Override
    public void UpdateJourneyFeedback(Long journeyId, Long userId, LocalDate targetDate) {

        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_NOT_FOUND));

        if (!journey.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.JOURNEY_FORBIDDEN);
        }


        JourneyFeedback feedback = journeyFeedbackRepository
                .findByJourneyId(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_FEEDBACK_NOT_FOUND));

        // 오늘 rate 계산
        int targetRate = calculateTodayRate(journeyId, targetDate);

        // 오늘이 루틴중 몇일차인지 계산
        int dayIndex = calculateDayIndex(journey, targetDate);

        // feedback 에반영
        feedback.updateDailyRate(dayIndex, targetRate);

        // totalRate 재계산
        int totalRate = calculateTotalRate(
                feedback.getDay1Rate(),
                feedback.getDay2Rate(),
                feedback.getDay3Rate()
        );

        feedback.complete(totalRate);

        // journey 완료 처리 -> 오늘이 끝나는 날이면
        boolean hasInProgress = routineProgressRepository
                .existsInProgress(journeyId, ProgressStatus.IN_PROGRESS);

        if (!hasInProgress && Objects.equals(journey.getEndDate(), LocalDate.now())) {
            journey.complete();
        }
    }

    private int calculateTodayRate(Long journeyId, LocalDate targetDate) {


        List<RoutineProgress> progresses =
                routineProgressRepository
                        .findByRoutine_Journey_IdAndProgressDate(journeyId, targetDate);


        long completedCount = progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();

        return (int) ((completedCount / 3.0) * 100);
    }

    private int calculateDayIndex(Journey journey, LocalDate targetDate) {
        return (int) (
                targetDate.toEpochDay()
                        - journey.getStartDate().toEpochDay()
        ) + 1;
    }

    //전체 rate 계산 메서드
    private int calculateTotalRate(Integer day1, Integer day2, Integer day3) {

        int sum = 0;


        if (day1 != null) {
            sum += day1;
        }

        if (day2 != null) {
            sum += day2;
        }

        if (day3 != null) {
            sum += day3;
        }

        return  sum / 3;
    }
}
