package com.loopon.auth.presentation.docs;

import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.VerificationRequest;
import com.loopon.auth.application.dto.request.VerificationVerifyRequest;
import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.docs.error.errors.CommonUnAuthorizedResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "2. 인증(Auth)", description = "이메일 인증 및 비밀번호 재설정 API")
public interface VerificationApiDocs {

    @Operation(summary = "인증 번호 발송", description = "요청한 이메일로 4자리 인증 번호를 발송합니다. (유효시간 5분)<br>단기간 내 과도한 요청 시 차단될 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "발송 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> sendVerifyCode(@RequestBody VerificationRequest request);

    @Operation(summary = "인증 번호 검증", description = "이메일로 받은 인증 번호를 검증합니다.<br>검증 성공 시, 비밀번호 재설정 목적이라면 **resetToken(일회용 티켓)**을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "검증 성공 (resetToken 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<String>> verifyCode(@RequestBody VerificationVerifyRequest request);

    @Operation(summary = "비밀번호 재설정", description = "검증 단계에서 발급받은 **resetToken**을 사용하여 비밀번호를 변경합니다.<br>토큰은 일회용이며 사용 즉시 만료됩니다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonUnAuthorizedResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> resetPassword(@RequestBody PasswordResetRequest request);
}
