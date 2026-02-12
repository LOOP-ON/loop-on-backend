package com.loopon.routine.application.service;

import com.loopon.journey.domain.ProgressStatus;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.RoutineProgress;
import com.loopon.routine.domain.service.RoutineQueryService;
import com.loopon.routine.infrastructure.RoutineProgressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 루틴 진행 정보입니다."));

        // 본인 여정인지 검색
        if (!progress.getRoutine()
                .getJourney()
                .getUser()
                .getId()
                .equals(userId)) {
            throw new IllegalArgumentException("해당 유저의 루틴이 아닙니다.");
        }

        // 미루기 상태 확인
        if (progress.getStatus() != ProgressStatus.POSTPONED) {
            throw new IllegalStateException("미루기 상태가 아닙니다.");
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
