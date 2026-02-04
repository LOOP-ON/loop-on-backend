package com.loopon.challenge.application.dto.command;

import lombok.Builder;

@Builder
public record ChallengeLikeCommentCommand(
        Long commentId,
        Long userId
) {
}
