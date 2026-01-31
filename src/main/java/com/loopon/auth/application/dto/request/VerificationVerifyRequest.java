package com.loopon.auth.application.dto.request;

import com.loopon.auth.domain.VerificationPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerificationVerifyRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        String code,

        @NotNull
        VerificationPurpose purpose
) {
}
