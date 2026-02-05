package com.loopon.challenge.application.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record ChallengeCombinedViewResponse(
        Slice<ChallengeViewResponse> trendingChallenges,
        Slice<ChallengeViewResponse> friendChallenges
) {
}
