package com.loopon.challenge.application.dto.response;

import lombok.Builder;

@Builder
public record ChallengePreviewResponse(
        Long challengeId,
        String imageUrl
) {
}
