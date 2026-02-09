package com.loopon.journey.application.service;

import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import com.loopon.journey.application.dto.command.JourneyCommand;
import com.loopon.journey.application.dto.converter.JourneyConverter;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.journey.domain.ProgressStatus;
import com.loopon.journey.domain.service.JourneyCommandService;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyCommandServiceImpl implements JourneyCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final UserJpaRepository userRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;

    @Override
    @Transactional
    public JourneyResponse.PostJourneyGoalDto postJourneyGoal(
            JourneyCommand.AddJourneyGoalCommand command
    ) {
        //사용자 찾기
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        //사용자가 현재 진행중인 여정이 있는지 검사
        journeyRepository.findByUserAndStatus(user, JourneyStatus.IN_PROGRESS)
                .ifPresent(e -> {
                    throw new IllegalArgumentException("이미 진행중인 여정이 있습니다.");
                });

        //여정 객체 생성
        Journey journey = JourneyConverter.commandToJourney(command, user);

        //여정 생성
        journeyRepository.save(journey);

        return new JourneyResponse.PostJourneyGoalDto(journey.getId());
    }

    ;

    @Transactional
    @Override
    public JourneyResponse.PostponeRoutineDto postponeRoutine(JourneyCommand.PostponeRoutineCommand command) {

        //여정 조회 후 진행중인지 판단.
        Journey journey = journeyRepository.getById(command.journeyId());

        if (journey.getStatus() != JourneyStatus.IN_PROGRESS) {
            throw new IllegalStateException("진행 중인 여정이 아닙니다.");
        }

        //요청한 progress들 중에 IN_PROGRESS인 경우만 조회
        List<RoutineProgress> progresses =
                routineProgressRepository
                        .findAllByIdInAndStatus(
                                command.routineProgressIds(),
                                ProgressStatus.IN_PROGRESS
                        );

        if (progresses.isEmpty()) {
            throw new IllegalStateException("미룰 수 있는 루틴 진행 정보가 없습니다.");
        }

        //각 프로그레스의 postpone 이유 입력
        progresses.forEach(progress ->
                progress.postpone(command.reason())
        );

        return new JourneyResponse.PostponeRoutineDto(
                progresses.stream()
                        .map(RoutineProgress::getId)
                        .distinct()
                        .toList(),
                command.reason()
        );
    }
}
