package com.loopon.challenge.application.dto.response;

import com.loopon.global.domain.dto.SliceResponse;
import lombok.Builder;

@Builder
public record ChallengeCombinedViewResponse(
        SliceResponse<ChallengeViewResponse> trendingChallenges,
        SliceResponse<ChallengeViewResponse> friendChallenges
) {
}
