package com.loopon.routine.domain.service;

import com.loopon.routine.application.dto.response.RoutineResponse;
import org.springframework.transaction.annotation.Transactional;

public interface RoutineQueryService {
    @Transactional
    RoutineResponse.RoutinePostponeReasonDto getPostponeReason(
            Long progressId,
            Long userId
    );
}
