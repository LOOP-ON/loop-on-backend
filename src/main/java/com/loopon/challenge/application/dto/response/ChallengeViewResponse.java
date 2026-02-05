package com.loopon.challenge.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ChallengeViewResponse(
        Long challengeId,
        Integer journeySequence,
        List<String> imageUrls,
        String content,
        List<String> hashtags,
        LocalDateTime createdAt,
        String nickname,
        String profileImageUrl,
        Boolean isLiked,
        Integer likeCount
) {
}
