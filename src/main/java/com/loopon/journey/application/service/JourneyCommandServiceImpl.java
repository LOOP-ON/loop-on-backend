package com.loopon.journey.application.service;

import com.loopon.Routine.domain.Routine;
import com.loopon.Routine.domain.RoutineProgress;
import com.loopon.Routine.infrastructure.RoutineJpaRepository;
import com.loopon.Routine.infrastructure.RoutineProgressJpaRepository;
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

        // 해당 여정에 속한 완료되지 않은 루틴인지 판단
        Routine routine = routineRepository
                .findByIdAndJourneyId(command.journeyId(), command.routineId())
                .orElseThrow(() -> new IllegalArgumentException("해당 여정에 속한 루틴이 아닙니다."));

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 루틴 프로그레스 조회
        RoutineProgress progress = routineProgressRepository
                .findByRoutineAndProgressDate(routine, today)
                .orElseThrow(() -> new IllegalStateException("오늘의 루틴 진행 정보가 없습니다."));

        //이미 완료된 루틴일 경우 미루기 불가
        if (progress.getStatus() == ProgressStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료 된 루틴입니다.");
        }

        // 3️⃣ 상태 변경
        progress.postpone(command.reason());

        return new JourneyResponse.PostponeRoutineDto(
                routine.getId(),
                command.reason());
    }
}
