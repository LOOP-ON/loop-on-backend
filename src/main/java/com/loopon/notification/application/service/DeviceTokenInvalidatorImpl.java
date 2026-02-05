package com.loopon.notification.application.service;

import com.loopon.notification.domain.repository.DeviceTokenRepository;
import com.loopon.notification.domain.service.DeviceTokenInvalidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenInvalidatorImpl implements DeviceTokenInvalidator {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    @Transactional
    public void invalidate(String deviceToken, String reason) {
        deviceTokenRepository.deleteByToken(deviceToken);
    }
}
