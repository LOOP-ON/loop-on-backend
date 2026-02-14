package com.loopon.routine.application.service;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.domain.ProgressStatus;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.service.RoutineQueryService;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoutineQueryServiceImpl implements RoutineQueryService {
    private final RoutineProgressJpaRepository routineProgressRepository;

    @Transactional
    @Override
    public RoutineResponse.RoutinePostponeReasonDto getPostponeReason(
            Long progressId,
            Long userId
    ) {

        // progress ID로 조회
        RoutineProgress progress = routineProgressRepository.findById(progressId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        // 본인 여정인지 검색
        if (!progress.getRoutine()
                .getJourney()
                .getUser()
                .getId()
                .equals(userId)) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        // 미루기 상태 확인
        if (progress.getStatus() != ProgressStatus.POSTPONED) {
            throw new BusinessException(ErrorCode.ROUTINE_NOT_POSTPONABLE);
        }

        // 루틴 내용 가져오기
        String routineContent = progress.getRoutine().getContent();

        // 미루기 사유 가져오기
        String reason = progress.getPostponedReason();

        return new RoutineResponse.RoutinePostponeReasonDto(
                progress.getId(),
                routineContent,
                reason
        );
    }
}
