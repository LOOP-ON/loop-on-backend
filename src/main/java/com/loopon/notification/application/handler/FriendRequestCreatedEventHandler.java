package com.loopon.notification.application.handler;


import com.loopon.notification.application.event.FriendRequestCreatedEvent;
import com.loopon.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FriendRequestCreatedEventHandler {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FriendRequestCreatedEvent event) {
        notificationService.sendFriendRequestPush(
                event.toUserId(),
                event.fromUserId(),
                event.friendRequestId()
        );
    }
}
