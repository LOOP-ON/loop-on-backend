package com.loopon.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordEmailRequest(
        @NotBlank
        @Email
        String email
) {
}
