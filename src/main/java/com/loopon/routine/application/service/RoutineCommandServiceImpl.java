package com.loopon.routine.application.service;

import com.loopon.routine.application.dto.converter.RoutineConverter;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.Routine;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.infrastructure.RoutineJpaRepository;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.infrastructure.JourneyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final JourneyJpaRepository journeyRepository;
    private final RoutineJpaRepository routineRepository;

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

        //루틴 알림 테이블 생성 -> TODO : 프론트와 상의 후 추후 결정

        //response 형태 맞추어 리턴
        return RoutineConverter.toPostRoutinesDto(routines);
    }

}
