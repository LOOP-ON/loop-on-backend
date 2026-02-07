package com.loopon.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @Schema(description = "현재 비밀번호", example = "OldPass123!")
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        String currentPassword,

        @Schema(description = "비밀번호 (영문/숫자/특수문자 포함 8~20자)", example = "P@ssword123!")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다."
        )
        String newPassword,

        @Schema(description = "비밀번호 확인", example = "P@ssword123!")
        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        String confirmNewPassword
) {
}
