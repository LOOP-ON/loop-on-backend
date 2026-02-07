package com.loopon.notification.application.handler;

import com.loopon.notification.application.event.ChallengeLikeEvent;
import com.loopon.notification.application.service.ChallengeLikeAggregationService;
import com.loopon.notification.infrastructure.redis.ChallengeLikeAggregationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChallengeLikeEventHandler {

    private final ChallengeLikeAggregationRedisRepository likeRepo;
    private final ChallengeLikeAggregationService aggregationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikePush(ChallengeLikeEvent event) {
        if (event.challengeOwnerId().equals(event.likedUserId())) return;

        long count = likeRepo.increment(event.challengeId());

        if (count == 1L) {
            aggregationService.scheduleLikePush(event.challengeId(), event.challengeOwnerId());
        }
    }
}