package com.loopon.notification.application.service;

import com.loopon.challenge.infrastructure.jpa.ChallengeJpaRepository;
import com.loopon.global.domain.EnvironmentType;
import com.loopon.notification.domain.repository.DeviceTokenRepository;
import com.loopon.notification.domain.service.NotificationService;
import com.loopon.notification.infrastructure.apns.APNsPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final APNsPushService apnsPushService;
    private  final ChallengeJpaRepository challengeJpaRepository;
    EnvironmentType env = EnvironmentType.PROD;
    @Override
    public void sendFriendRequestPush(Long receiverId, Long senderId, Long friendRequestId) {
        deviceTokenRepository.findByUserIdAndEnvironmentType(receiverId, env)
                .ifPresent(token -> {
                    Map<String, String> data = Map.of(
                            "type", "FRIEND_REQUEST",
                            "friendRequestId", String.valueOf(friendRequestId),
                            "senderId", String.valueOf(senderId)
                    );

                    apnsPushService.send(
                            token.getToken(),
                            "친구 신청 알림",
                            " ✴️새로운 친구 요청이 있어요",
                            data
                    );
                });
    }
    @Override
    public void sendChallengeLikePush(   Long challengeId, Long challengeOwnerId, int likeCount) {
        deviceTokenRepository.findByUserIdAndEnvironmentType(challengeOwnerId, env)
                .ifPresent(token -> {
                    Map<String, String> data = Map.of(
                            "type", "CHALLENGE_LIKE",
                            "challengeId", String.valueOf(challengeId),
                            "likeCount", String.valueOf(likeCount)
                    );

                    apnsPushService.send(
                            token.getToken(),
                            "좋아요 알림",
                            " ✴️회원님의 챌린지에 새로운 n개의 좋아요가 있어요",
                            data
                    );
                });
    }
    @Override
    public void sendChallengeCommentPush(Long challengeId,Long challengeOwnerId, Long commentedUserId) {
        deviceTokenRepository.findByUserIdAndEnvironmentType(challengeOwnerId, env)
                .ifPresent(token -> {
                    Map<String, String> data = Map.of(
                            "type", "CHALLENGE_COMMENT",
                            "challengeId", String.valueOf(challengeId),
                            "commentedUserId", String.valueOf(commentedUserId)
                    );

                    apnsPushService.send(
                            token.getToken(),
                            "댓글 알림",
                            " ✴️회원님의 챌린지에 새로운 댓글이 있어요",
                            data
                    );
                });
    }
}
