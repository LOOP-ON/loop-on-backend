package com.loopon.auth.application.dto.request;

import com.loopon.user.domain.UserProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
        @NotNull(message = "소셜 로그인 제공자를 선택해주세요.")
        UserProvider provider,

        @NotBlank(message = "액세스 토큰을 입력해주세요.")
        String accessToken
) {
}
