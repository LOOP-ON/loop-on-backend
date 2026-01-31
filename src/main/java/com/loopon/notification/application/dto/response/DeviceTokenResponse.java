package com.loopon.notification.application.dto.response;

public record DeviceTokenResponse(
        Long deviceTokenId,
        boolean isNew
) { }