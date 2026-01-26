package com.loopon.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordVerifyRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank String code
) {
}
