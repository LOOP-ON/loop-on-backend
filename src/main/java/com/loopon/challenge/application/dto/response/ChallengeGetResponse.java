package com.loopon.challenge.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChallengeGetResponse(
        Long challengeId,

        List<String> imageList,

        List<String> hashtagList,

        String content,

        Long expeditionId
) {
}
