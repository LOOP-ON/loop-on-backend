package com.loopon.auth.application.dto.request;

import com.loopon.user.domain.UserProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
        @NotNull UserProvider provider,
        @NotBlank String accessToken
) {
}
