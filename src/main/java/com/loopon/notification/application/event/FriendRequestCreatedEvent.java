package com.loopon.notification.application.event;

public record FriendRequestCreatedEvent(
        Long friendRequestId,
        Long fromUserId,
        Long toUserId
) {
}