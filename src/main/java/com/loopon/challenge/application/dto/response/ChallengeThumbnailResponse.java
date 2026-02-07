package com.loopon.challenge.application.dto.response;

import com.loopon.challenge.domain.ChallengeImage;

public record ChallengeThumbnailResponse(
        Long challengeId,
        String repImageUrl
) {

    public static ChallengeThumbnailResponse from(ChallengeImage challengeImage) {
        return new ChallengeThumbnailResponse(
                challengeImage.getChallenge().getId(),
                challengeImage.getImageUrl()
        );
    }
}
