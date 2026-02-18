package com.loopon.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResult(
        @Schema(description = "액세스 토큰 (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "d1f2e3c4b5a6...")
        String refreshToken
) {

    public static AuthResult of(String accessToken, String refreshToken) {
        return new AuthResult(accessToken, refreshToken);
    }
}
