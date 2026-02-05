package com.loopon.notification.application.service;

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

    @Override
    public void sendFriendRequestPush(Long receiverId, Long senderId, Long friendRequestId) {
        EnvironmentType env = EnvironmentType.PROD;

        deviceTokenRepository.findByUserIdAndEnvironmentType(receiverId, env)
                .ifPresent(token -> {
                    Map<String, String> data = Map.of(
                            "type", "FRIEND_REQUEST",
                            "friendRequestId", String.valueOf(friendRequestId),
                            "senderId", String.valueOf(senderId)
                    );

                    apnsPushService.send(
                            token.getToken(),
                            "친구 요청",
                            "새로운 친구 요청이 도착했습니다.",
                            data
                    );
                });

    }
}
