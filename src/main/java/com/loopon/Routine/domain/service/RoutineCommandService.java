package com.loopon.Routine.domain.service;

import com.loopon.Routine.application.dto.request.RoutineRequest;
import com.loopon.Routine.application.dto.response.RoutineResponse;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineCommandService {

    @Transactional
    RoutineResponse.PostRoutinesDto postRoutine(
            RoutineRequest.AddJRoutineDto routineRequest
    );
}
