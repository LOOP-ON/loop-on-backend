package com.loopon.auth.application.dto.request;

import com.loopon.auth.domain.VerificationPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerificationVerifyRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,

        @NotBlank(message = "검증 코드를 입력해주세요.")
        String code,

        @NotNull(message = "검증 목적을 선택해주세요.")
        VerificationPurpose purpose
) {
}
