package com.loopon.notification;

import com.loopon.notification.application.service.DeviceTokenInvalidatorImpl;
import com.loopon.notification.domain.repository.DeviceTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenInvalidatorImplTest {

    @Mock DeviceTokenRepository deviceTokenRepository;

    @Test
    @DisplayName("invalidate: reason과 무관하게 deleteByToken 호출")
    void invalidate_callsDeleteByToken() {

        DeviceTokenInvalidatorImpl sut = new DeviceTokenInvalidatorImpl(deviceTokenRepository);

        sut.invalidate("token-abc", "410 Gone");

        then(deviceTokenRepository).should(times(1))
                .deleteByToken("token-abc");
    }
}