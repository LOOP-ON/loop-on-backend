package com.loopon.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank String email,
        @NotBlank String resetToken,

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        String newPassword,

        @NotBlank String confirmPassword
) {
}
