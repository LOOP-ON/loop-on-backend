package com.loopon.journey.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.journey.application.dto.response.JourneyContinueResponse;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JourneyContinueService {

    private final JourneyRepository journeyRepository;

    public JourneyContinueResponse continueJourney(Long journeyId, Long userId) {
        // 1. 기존 여정 조회
        Journey originalJourney = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOURNEY_NOT_FOUND));

        // 2. 권한 확인
        if (!originalJourney.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.JOURNEY_FORBIDDEN);
        }

        // 3. 기존 목표를 그대로 반환 (새로운 여정 생성을 위한 데이터)
        return JourneyContinueResponse.builder()
                .goal(originalJourney.getGoal())
                .isContinuation(true)
                .originalJourneyId(originalJourney.getId())
                .build();
    }
}