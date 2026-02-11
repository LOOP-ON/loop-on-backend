package com.loopon.routine.domain.service;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface RoutineCommandService {

    RoutineResponse.PostRoutinesDto postRoutine(
            Long userId,
            RoutineRequest.CreateJourneyWithRoutineDto request
    );

    @Transactional
        // 루틴 인증 파일 업로드
    RoutineResponse.RoutineCertifyDto certifyRoutine(Long progressId, Long userId, MultipartFile image);
}
