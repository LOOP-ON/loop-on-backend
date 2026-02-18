package com.loopon.expedition.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ExpeditionChallengesResponse(
        Long challengeId,
        Integer journeyNumber,
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