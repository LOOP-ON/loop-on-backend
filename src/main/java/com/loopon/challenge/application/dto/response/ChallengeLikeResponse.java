package com.loopon.challenge.application.dto.response;

import lombok.Builder;

@Builder
public record ChallengeLikeResponse(
        Long challengeId,
        Long challengeLikeId
) {
}
