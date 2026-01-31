package com.loopon.notification.application.service;

import com.loopon.global.domain.EnvironmentType;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.notification.application.dto.response.DeviceTokenResponse;
import com.loopon.notification.domain.DeviceToken;
import com.loopon.notification.domain.repository.DeviceTokenRepository;
import com.loopon.notification.domain.service.DeviceTokenService;
import com.loopon.user.domain.User;
import com.loopon.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenServiceImpl implements DeviceTokenService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional(readOnly = true)
    @Override
    public DeviceTokenResponse saveDeviceToken(Long me, DeviceTokenRequest req) {

        User userRef = userJpaRepository.getReferenceById(me);
        String token = req.deviceToken();
        EnvironmentType env = req.environmentType();

        DeviceToken deviceToken = deviceTokenRepository
                .findByTokenAndEnvironmentType(token, env)
                .orElseGet(() ->
                        deviceTokenRepository.findByUserIdAndEnvironmentType(me, env)
                                .orElseGet(() -> DeviceToken.create(userRef, req)));

        boolean isNew = deviceToken.getId() == null;

        deviceToken.refresh(userRef, req.deviceToken());

        DeviceToken saved = deviceTokenRepository.save(deviceToken);

        return new DeviceTokenResponse(saved.getId(), isNew);
    }

    @Transactional
    @Override
    public void deleteDeviceToken(Long me, DeviceTokenRequest deviceTokenRequest) {

        DeviceToken deviceToken = deviceTokenRepository
                .findByUserIdAndToken(me, deviceTokenRequest.deviceToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.DEVICE_TOKEN_NOT_FOUND));

        deviceTokenRepository.delete(deviceToken);
    }


}
