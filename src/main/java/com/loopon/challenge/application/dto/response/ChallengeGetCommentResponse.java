package com.loopon.challenge.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChallengeGetCommentResponse(
        Long commentId,
        String nickName,
        String profileImageUrl,
        String content,
        Integer likeCount,
        List<ChallengeGetCommentResponse> children
) {
}
