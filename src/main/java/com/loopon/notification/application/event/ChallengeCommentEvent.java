package com.loopon.notification.application.event;

public record ChallengeCommentEvent(
        Long challengeId,
        Long challengeOwnerId,
        Long commentedUserId
) {
}