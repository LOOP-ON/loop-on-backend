package com.loopon.notification.domain.service;

public interface DeviceTokenInvalidator {
    void invalidate(String deviceToken, String reason);
}
