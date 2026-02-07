package com.loopon.routine.domain.service;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineCommandService {

    @Transactional
    RoutineResponse.PostRoutinesDto postRoutine(
            RoutineRequest.AddJRoutineDto routineRequest
    );
}
