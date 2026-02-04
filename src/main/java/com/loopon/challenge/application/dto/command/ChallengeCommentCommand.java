package com.loopon.challenge.application.dto.command;

import lombok.Builder;

@Builder
public record ChallengeCommentCommand(
        Long challengeId,
        Long userId,
        String content,
        Long parentId
) {}
