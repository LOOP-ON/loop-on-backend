package com.loopon.notification.application.event;

public record ChallengeLikeEvent(
        Long challengeId,
        Long challengeOwnerId,
        Long likedUserId
) {
}
