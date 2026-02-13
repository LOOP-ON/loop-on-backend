package com.loopon.routine.domain.service;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RoutineCommandService {

    RoutineResponse.PostRoutinesDto postRoutine(
            Long userId,
            RoutineRequest.CreateJourneyWithRoutineDto request
    );

    RoutineResponse.RoutineCertifyDto certifyRoutine(
            Long progressId,
            Long userId,
            MultipartFile image
    );

    RoutineResponse.RoutinePostponeReasonEditDto editPostponeReason(
            Long progressId,
            Long userId,
            RoutineRequest.editReasonDto newReason
    );
}
