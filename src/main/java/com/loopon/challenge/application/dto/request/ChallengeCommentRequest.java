package com.loopon.challenge.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChallengeCommentRequest(
        @NotNull String content,
        Long parentId
) {
}
