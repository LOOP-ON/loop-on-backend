package com.loopon.notification.application.dto.request;

import com.loopon.global.domain.EnvironmentType;
import jakarta.validation.constraints.NotNull;

public record DeviceTokenRequest (
    @NotNull
    String deviceToken,
    @NotNull
    EnvironmentType environmentType
){}
