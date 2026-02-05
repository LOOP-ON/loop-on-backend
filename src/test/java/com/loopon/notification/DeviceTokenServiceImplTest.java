package com.loopon.notification;

import com.loopon.global.domain.EnvironmentType;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.notification.application.dto.response.DeviceTokenResponse;
import com.loopon.notification.application.service.DeviceTokenServiceImpl;
import com.loopon.notification.domain.DeviceToken;
import com.loopon.notification.domain.repository.DeviceTokenRepository;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenServiceImplTest {

    @Mock
    DeviceTokenRepository deviceTokenRepository;
    @Mock
    UserJpaRepository userJpaRepository;

    private DeviceTokenServiceImpl sut() {
        return new DeviceTokenServiceImpl(deviceTokenRepository, userJpaRepository);
    }

    @Test
    @DisplayName("saveDeviceToken: token+env로 이미 존재하면 그 엔티티를 사용하고 isNew=false로 반환")
    void saveDeviceToken_whenTokenExists_useIt_andIsNewFalse() {

        Long me = 1L;
        DeviceTokenRequest req = new DeviceTokenRequest("token-abc", EnvironmentType.DEV);

        User userRef = mock(User.class);
        given(userJpaRepository.getReferenceById(me)).willReturn(userRef);

        DeviceToken existing = mock(DeviceToken.class);
        given(existing.getId()).willReturn(5L);

        given(deviceTokenRepository.findByTokenAndEnvironmentType("token-abc", EnvironmentType.DEV))
                .willReturn(Optional.of(existing));

        DeviceToken saved = mock(DeviceToken.class);
        given(saved.getId()).willReturn(5L);
        given(deviceTokenRepository.save(existing)).willReturn(saved);

        DeviceTokenResponse res = sut().saveDeviceToken(me, req);

        assertEquals(5L, res.deviceTokenId());
        assertFalse(res.isNew());

        then(deviceTokenRepository).should(times(1))
                .findByTokenAndEnvironmentType("token-abc", EnvironmentType.DEV);
        then(deviceTokenRepository).should(never())
                .findByUserIdAndEnvironmentType(anyLong(), any());
        then(existing).should(times(1))
                .refresh(eq(userRef), eq("token-abc"));
        then(deviceTokenRepository).should(times(1))
                .save(existing);
    }

    @Test
    @DisplayName("saveDeviceToken: token+env로 없고 user+env로 존재하면 그 엔티티를 사용")
    void saveDeviceToken_whenTokenNotExists_butUserEnvExists_useUserEnvToken() {

        Long me = 1L;
        DeviceTokenRequest req = new DeviceTokenRequest("token-abc", EnvironmentType.PROD);

        User userRef = mock(User.class);
        given(userJpaRepository.getReferenceById(me)).willReturn(userRef);

        given(deviceTokenRepository.findByTokenAndEnvironmentType("token-abc", EnvironmentType.PROD))
                .willReturn(Optional.empty());

        DeviceToken byUserEnv = mock(DeviceToken.class);
        given(byUserEnv.getId()).willReturn(9L);

        given(deviceTokenRepository.findByUserIdAndEnvironmentType(me, EnvironmentType.PROD))
                .willReturn(Optional.of(byUserEnv));

        DeviceToken saved = mock(DeviceToken.class);
        given(saved.getId()).willReturn(9L);
        given(deviceTokenRepository.save(byUserEnv)).willReturn(saved);


        DeviceTokenResponse res = sut().saveDeviceToken(me, req);


        assertEquals(9L, res.deviceTokenId());
        assertFalse(res.isNew());

        then(deviceTokenRepository).should(times(1))
                .findByTokenAndEnvironmentType("token-abc", EnvironmentType.PROD);
        then(deviceTokenRepository).should(times(1))
                .findByUserIdAndEnvironmentType(me, EnvironmentType.PROD);
        then(byUserEnv).should(times(1))
                .refresh(eq(userRef), eq("token-abc"));
        then(deviceTokenRepository).should(times(1))
                .save(byUserEnv);
    }

    @Test
    @DisplayName("saveDeviceToken: token+env도 없고 user+env도 없으면 DeviceToken.create()로 신규 생성, isNew=true 반환")
    void saveDeviceToken_whenNoneExists_createNew_andIsNewTrue() {

        Long me = 1L;
        DeviceTokenRequest req = new DeviceTokenRequest("token-new", EnvironmentType.DEV);

        User userRef = mock(User.class);
        given(userJpaRepository.getReferenceById(me)).willReturn(userRef);

        given(deviceTokenRepository.findByTokenAndEnvironmentType("token-new", EnvironmentType.DEV))
                .willReturn(Optional.empty());
        given(deviceTokenRepository.findByUserIdAndEnvironmentType(me, EnvironmentType.DEV))
                .willReturn(Optional.empty());

        DeviceToken created = mock(DeviceToken.class);
        given(created.getId()).willReturn(null);

        DeviceToken saved = mock(DeviceToken.class);
        given(saved.getId()).willReturn(10L);
        given(deviceTokenRepository.save(created)).willReturn(saved);

        try (MockedStatic<DeviceToken> mocked = mockStatic(DeviceToken.class)) {
            mocked.when(() -> DeviceToken.create(eq(userRef), eq(req)))
                    .thenReturn(created);

            DeviceTokenResponse res = sut().saveDeviceToken(me, req);

            assertEquals(10L, res.deviceTokenId());
            assertTrue(res.isNew());

            mocked.verify(() -> DeviceToken.create(eq(userRef), eq(req)), times(1));
        }

        then(created).should(times(1))
                .refresh(eq(userRef), eq("token-new"));
        then(deviceTokenRepository).should(times(1))
                .save(created);
    }

    @Test
    @DisplayName("deleteDeviceToken: userId+token으로 찾으면 delete 호출")
    void deleteDeviceToken_success() {
        Long me = 1L;
        DeviceTokenRequest req = new DeviceTokenRequest("token-abc", EnvironmentType.DEV);

        DeviceToken found = mock(DeviceToken.class);
        given(deviceTokenRepository.findByUserIdAndToken(me, "token-abc"))
                .willReturn(Optional.of(found));

        sut().deleteDeviceToken(me, req);

        then(deviceTokenRepository).should(times(1))
                .delete(found);
    }

    @Test
    @DisplayName("deleteDeviceToken: 없으면 BusinessException(ErrorCode.DEVICE_TOKEN_NOT_FOUND)")
    void deleteDeviceToken_notFound_throwBusinessException() {
        Long me = 1L;
        DeviceTokenRequest req = new DeviceTokenRequest("nope", EnvironmentType.DEV);

        given(deviceTokenRepository.findByUserIdAndToken(me, "nope"))
                .willReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> sut().deleteDeviceToken(me, req));

        assertEquals(ErrorCode.DEVICE_TOKEN_NOT_FOUND, ex.getErrorCode());
        then(deviceTokenRepository).should(never()).delete(any());
    }
}