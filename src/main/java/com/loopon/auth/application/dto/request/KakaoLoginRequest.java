package com.loopon.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @NotBlank String accessToken
) {
}
