package com.loopon.auth.presentation.docs;

import com.loopon.auth.application.dto.request.KakaoLoginRequest;
import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.application.dto.response.AccessTokenResponse;
import com.loopon.auth.application.dto.response.LoginSuccessResponse;
import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.docs.error.errors.CommonUnAuthorizedResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "2. 인증(Auth)", description = "인증 관련 API")
public interface AuthApiDocs {

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 Access Token 및 Refresh Token을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<LoginSuccessResponse>> login(@RequestBody LoginRequest request);

    @Operation(summary = "카카오 로그인", description = "카카오 액세스 토큰으로 로그인하여 Access Token 및 Refresh Token을 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<LoginSuccessResponse>> loginKakao(@RequestBody KakaoLoginRequest request);

    @Operation(summary = "토큰 재발급 (RTR)", description = "쿠키에 담긴 Refresh Token을 이용하여 Access Token을 재발급받습니다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공 (새로운 Access Token 반환)", useReturnTypeSchema = true)
    @CommonUnAuthorizedResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<AccessTokenResponse>> reissueTokens(
            @Parameter(description = "리프레시 토큰 (HttpOnly Cookie)", required = true, in = ParameterIn.COOKIE, schema = @Schema(type = "string"))
            String refreshToken,
            @Parameter(hidden = true)
            HttpServletResponse response
    );

    @Operation(summary = "로그아웃", description = "서버에서 Refresh Token을 삭제하고, 클라이언트 쿠키를 만료시킵니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", useReturnTypeSchema = true)
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> logout(
            @Parameter(description = "리프레시 토큰 (HttpOnly Cookie)", required = false, in = ParameterIn.COOKIE)
            String refreshToken
    );
}
