package com.loopon.notification;

import com.loopon.global.domain.EnvironmentType;
import com.loopon.notification.application.service.NotificationServiceImpl;
import com.loopon.notification.domain.DeviceToken;
import com.loopon.notification.domain.repository.DeviceTokenRepository;
import com.loopon.notification.infrastructure.apns.APNsPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock DeviceTokenRepository deviceTokenRepository;
    @Mock APNsPushService apnsPushService;

    private NotificationServiceImpl sut() {
        return new NotificationServiceImpl(deviceTokenRepository, apnsPushService);
    }

    @Test
    @DisplayName("토큰이 있으면 FRIEND_REQUEST 푸시를 전송한다")
    void sendFriendRequestPush_whenTokenExists_sendPush() {
        // given
        Long receiverId = 10L;
        Long senderId = 20L;
        Long friendRequestId = 30L;

        DeviceToken token = mock(DeviceToken.class);
        given(token.getToken()).willReturn("apns-token-xyz");

        given(deviceTokenRepository.findByUserIdAndEnvironmentType(receiverId, EnvironmentType.PROD))
                .willReturn(Optional.of(token));

        sut().sendFriendRequestPush(receiverId, senderId, friendRequestId);

        then(deviceTokenRepository).should(times(1))
                .findByUserIdAndEnvironmentType(receiverId, EnvironmentType.PROD);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> dataCaptor = ArgumentCaptor.forClass((Class) Map.class);

        then(apnsPushService).should(times(1)).send(
                eq("apns-token-xyz"),
                eq("친구 요청"),
                eq("새로운 친구 요청이 도착했습니다."),
                dataCaptor.capture()
        );

        Map<String, String> data = dataCaptor.getValue();
        assertEquals("FRIEND_REQUEST", data.get("type"));
        assertEquals(String.valueOf(friendRequestId), data.get("friendRequestId"));
        assertEquals(String.valueOf(senderId), data.get("senderId"));

        then(apnsPushService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("토큰이 없으면 푸시를 전송하지 않는다")
    void sendFriendRequestPush_whenNoToken_doNothing() {
        Long receiverId = 10L;
        Long senderId = 20L;
        Long friendRequestId = 30L;

        given(deviceTokenRepository.findByUserIdAndEnvironmentType(receiverId, EnvironmentType.PROD))
                .willReturn(Optional.empty());

        sut().sendFriendRequestPush(receiverId, senderId, friendRequestId);

        then(deviceTokenRepository).should(times(1))
                .findByUserIdAndEnvironmentType(receiverId, EnvironmentType.PROD);

        then(apnsPushService).shouldHaveNoInteractions();
    }
}