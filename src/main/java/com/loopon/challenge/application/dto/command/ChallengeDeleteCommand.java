package com.loopon.challenge.application.dto.command;

import lombok.Builder;

@Builder
public record ChallengeDeleteCommand(
        Long challengeId,
        Long userId
) {
}
