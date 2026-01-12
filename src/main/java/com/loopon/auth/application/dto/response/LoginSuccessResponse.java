package com.loopon.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 응답")
public record LoginSuccessResponse(
        @Schema(description = "액세스 토큰 (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken
) {

    public static LoginSuccessResponse of(String accessToken) {
        return new LoginSuccessResponse(accessToken);
    }
}
