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

        //여정에 포함된 루틴 아이디를 불러오기 (단일 조회일 경우 routine 테이블 하나, 전체 미루기일 경우 list)
        List<Routine> routines = resolveRoutines(command, journey);

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 루틴 프로그레스 조회
        List<RoutineProgress> progresses =
                routineProgressRepository.findAllByRoutineInAndProgressDateAndStatus(
                        routines,
                        today,
                        ProgressStatus.IN_PROGRESS
                );

        //각 프로그레스의 postpone 이유 입력
        progresses.forEach(progress ->
                progress.postpone(command.reason())
        );

        return new JourneyResponse.PostponeRoutineDto(
                progresses.stream()
                        .map(p -> p.getRoutine().getId())
                        .distinct()
                        .toList(),
                command.reason()
        );
    }

    private List<Routine> resolveRoutines(
            JourneyCommand.PostponeRoutineCommand command,
            Journey journey
    ) {
        // routineId가 있는 경우 선택 미루기
        if (command.routineIds().isPresent()
                && !command.routineIds().get().isEmpty()) {

            List<Long> routineIds = command.routineIds().get();

            return routineRepository.findAllByIdInAndJourney(
                    routineIds,
                    journey
            );
        }

        // routineId가 없는 경우 전체 미루기
        return routineRepository.findAllByJourney(journey);
    }
}
