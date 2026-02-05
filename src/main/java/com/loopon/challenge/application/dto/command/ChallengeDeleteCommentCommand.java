package com.loopon.challenge.application.dto.command;

import lombok.Builder;

@Builder
public record ChallengeDeleteCommentCommand(
        Long commentId,
        Long userId,
        Long challengeId
) {
}
