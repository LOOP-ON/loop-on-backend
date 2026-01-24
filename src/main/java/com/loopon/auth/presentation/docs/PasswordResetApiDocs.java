package com.loopon.auth.presentation.docs;

import com.loopon.auth.application.dto.request.PasswordEmailRequest;
import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.PasswordVerifyRequest;
import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "2-1. 인증(Auth) - Password", description = "비밀번호 재설정 관련 API")
public interface PasswordResetApiDocs {

    @Operation(summary = "인증 코드 발송 요청", description = """
            사용자의 이메일로 4자리 인증 코드를 발송합니다.<br>
            인증 코드의 유효 시간은 **3분**입니다.
            """)
    @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @ApiResponse(responseCode = "404", description = "가입되지 않은 이메일", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<String>> sendAuthCode(@RequestBody PasswordEmailRequest request);


    @Operation(summary = "인증 코드 검증", description = """
            이메일로 받은 4자리 인증 코드를 검증합니다.<br>
            검증에 성공하면 **비밀번호 재설정 토큰(resetToken)**이 반환됩니다.
            """)
    @ApiResponse(responseCode = "200", description = "검증 성공 및 토큰 발급", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<String>> verifyAuthCode(@RequestBody PasswordVerifyRequest request);


    @Operation(summary = "비밀번호 재설정", description = """
            발급받은 토큰과 새로운 비밀번호를 이용하여 비밀번호를 변경합니다.<br>
            **resetToken**은 [인증 코드 검증] 단계에서 발급받은 값을 사용해야 합니다.
            """)
    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<String>> resetPassword(@RequestBody PasswordResetRequest request);
}
