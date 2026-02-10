package com.loopon.notification.application.handler;

import com.loopon.notification.application.event.ChallengeCommentEvent;
import com.loopon.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChallengeCommentEventHandler {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentPush(ChallengeCommentEvent event) {
        notificationService.sendChallengeCommentPush(event.challengeId(), event.challengeOwnerId(), event.commentedUserId());
    }
}
