package com.loopon.notification.domain.service;

import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.notification.application.dto.response.DeviceTokenResponse;

public interface DeviceTokenService {
    DeviceTokenResponse saveDeviceToken(Long me, DeviceTokenRequest deviceTokenRequest);

    void deleteDeviceToken(Long me, DeviceTokenRequest deviceTokenRequest);
}
