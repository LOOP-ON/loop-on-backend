package com.loopon.auth.presentation;

import com.loopon.auth.application.PasswordResetService;
import com.loopon.auth.application.dto.request.PasswordEmailRequest;
import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.PasswordVerifyRequest;
import com.loopon.auth.presentation.docs.PasswordResetApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetApiController implements PasswordResetApiDocs {
    private final PasswordResetService passwordResetService;

    @Override
    @PostMapping("/email-request")
    public ResponseEntity<CommonResponse<String>> sendAuthCode(@RequestBody @Valid PasswordEmailRequest request) {
        passwordResetService.sendAuthCode(request);
        return ResponseEntity.ok(CommonResponse.onSuccess("인증 번호가 이메일로 발송되었습니다."));
    }

    @Override
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyAuthCode(@RequestBody @Valid PasswordVerifyRequest request) {
        String resetToken = passwordResetService.verifyAuthCode(request);
        return ResponseEntity.ok(CommonResponse.onSuccess(resetToken));
    }

    @Override
    @PatchMapping("/reset")
    public ResponseEntity<CommonResponse<String>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(CommonResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다."));
    }
}
