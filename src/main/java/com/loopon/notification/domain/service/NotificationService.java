package com.loopon.notification.domain.service;

public interface NotificationService {
    void sendFriendRequestPush(Long receiverId, Long senderId, Long friendRequestId);

    void sendChallengeLikePush(Long challengeId, Long challengeOwnerId, int likeCount);

    void sendChallengeCommentPush(Long challengeId, Long challengeOwnerId, Long likedUserId);
}
