package com.loopon.challenge.application.dto.command;

import lombok.Builder;

@Builder
public record ChallengeLikeCommand(
        Long challengeId,
        Boolean isLiked,
        Long userId
) {
}
