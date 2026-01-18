package com.loopon.challenge.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChallengeGetResponse(
        List<String> imageList,

        List<String> hashtagList,

        String content,

        Long expeditionId
) {}
