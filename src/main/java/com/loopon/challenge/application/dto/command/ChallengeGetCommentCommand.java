package com.loopon.challenge.application.dto.command;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record ChallengeGetCommentCommand(
        Long challengeId,
        Long userId,
        Pageable pageable
) {
}
