package com.loopon.notification.application.service;

import com.loopon.notification.domain.service.NotificationService;
import com.loopon.notification.infrastructure.redis.AggregationDelays;
import com.loopon.notification.infrastructure.redis.ChallengeLikeAggregationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChallengeLikeAggregationService {

    private final ChallengeLikeAggregationRedisRepository likeRepository;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    @Async
    public void scheduleLikePush(Long challengeId, Long ownerId) {
        Instant runAt = Instant.now().plusMillis(AggregationDelays.CHALLENGE_LIKE_MS);
        taskScheduler.schedule(() -> flushLikePush(challengeId, ownerId), runAt);
    }

    public void flushLikePush(Long challengeId, Long ownerId) {
        Integer likeCount = likeRepository.get(challengeId);
        if (likeCount == null || likeCount <= 0) return;

        notificationService.sendChallengeLikePush(challengeId, ownerId, likeCount);
        likeRepository.clear(challengeId);
    }
}
