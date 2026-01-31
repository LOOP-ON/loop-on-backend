package com.loopon.auth.presentation;

import com.loopon.auth.application.PasswordResetService;
import com.loopon.auth.application.VerificationService;
import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.VerificationRequest;
import com.loopon.auth.application.dto.request.VerificationVerifyRequest;
import com.loopon.auth.presentation.docs.VerificationApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
public class VerificationApiController implements VerificationApiDocs {
    private final VerificationService verificationService;
    private final PasswordResetService passwordResetService;

    @Override
    @PostMapping("/send")
    public ResponseEntity<CommonResponse<Void>> sendVerifyCode(@RequestBody @Valid VerificationRequest request) {
        verificationService.sendCode(request.email(), request.purpose());
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }

    @Override
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyCode(@RequestBody @Valid VerificationVerifyRequest request) {
        String resetToken = verificationService.verifyCode(request.email(), request.code(), request.purpose());
        return ResponseEntity.ok(CommonResponse.onSuccess(resetToken));
    }

    @Override
    @PostMapping("/password-reset")
    public ResponseEntity<CommonResponse<Void>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
