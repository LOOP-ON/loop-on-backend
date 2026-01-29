package com.loopon.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "액세스 토큰 재발급 응답")
public record AccessTokenResponse(
        @Schema(description = "액세스 토큰 (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken
) {

    public static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
