package com.loopon.routine.application.service;

import com.loopon.routine.application.dto.converter.RoutineConverter;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;
    private final RoutineProgressJpaRepository routineProgressRepository;

    @Transactional
    @Override
    public RoutineResponse.PostRoutinesDto postRoutine(
            RoutineRequest.AddJRoutineDto routineRequest
    ){
        //여정 찾기
        Journey journey = journeyRepository.getReferenceById(routineRequest.journeyId());

        //루틴 객체 생성
        List<Routine> routines = routineRequest.routines().stream()
                .map(dto -> RoutineConverter.bodyToRoutine(journey, dto))
                .toList();

        //루틴 생성
        List<Routine> routineList = routineRepository.saveAll(routines);

        //루틴 Progress 3일치 생성
        LocalDate today = LocalDate.now();

        List<RoutineProgress> progressList =
                routineList.stream()
                        .flatMap(routine ->
                                Stream.of(
                                            today,
                                            today.plusDays(1),
                                            today.plusDays(2)
                                        )
                                        .map(date ->
                                                RoutineProgress.create(routine, date)
                                        )
                        )
                        .toList();

        routineProgressRepository.saveAll(progressList);

        //response 형태 맞추어 리턴
        return RoutineConverter.toPostRoutinesDto(routines);
    }

}
