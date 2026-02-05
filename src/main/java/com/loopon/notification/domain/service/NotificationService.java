package com.loopon.notification.domain.service;

public interface NotificationService {
    void sendFriendRequestPush(Long receiverId, Long senderId, Long friendRequestId);
}
