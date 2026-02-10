package com.loopon.notification.application.service;

import com.loopon.notification.domain.service.NotificationService;
import com.loopon.notification.infrastructure.redis.AggregationDelays;
import com.loopon.notification.infrastructure.redis.ChallengeLikeAggregationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeLikeAggregationService {

    private final ChallengeLikeAggregationRedisRepository likeRepo;
    private final NotificationService notificationService;

    @Async
    public void scheduleLikePush(Long challengeId, Long ownerId) {
        try {
            Thread.sleep(AggregationDelays.CHALLENGE_LIKE_MS);

            Integer likeCount = likeRepo.get(challengeId);
            if (likeCount == null || likeCount <= 0) return;

            notificationService.sendChallengeLikePush(challengeId, ownerId, likeCount);
            likeRepo.clear(challengeId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}