package com.loopon.user.application.dto.request;

import com.loopon.user.application.dto.command.UserSignUpCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "회원가입 요청 DTO")
public record UserSignUpRequest(

        @Schema(description = "이메일", example = "loopon@test.com")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 254, message = "이메일은 254자 이하로 입력해주세요.")
        String email,

        @Schema(description = "비밀번호 (영문/숫자/특수문자 포함 8~20자)", example = "P@ssword123!")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다."
        )
        String password,

        @Schema(description = "비밀번호 확인", example = "P@ssword123!")
        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        String confirmPassword,

        @Schema(description = "닉네임", example = "LoopMaster")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 특수문자를 사용할 수 없습니다.")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl,

        @Schema(description = "동의한 약관 ID 목록", example = "[1, 2, 3, 6]")
        @NotNull(message = "약관 동의 목록은 필수입니다.")
        @Size(min = 1, message = "필수 약관에 동의해야 합니다.")
        List<Long> agreedTermIds
) {

    public UserSignUpCommand toCommand() {
        return UserSignUpCommand.of(
                email,
                password,
                confirmPassword,
                nickname,
                profileImageUrl,
                agreedTermIds
        );
    }

    @Override
    public String toString() {
        return "UserSignUpRequest[" +
                "email=" + email +
                ", nickname=" + nickname +
                ", agreedTermIds=" + agreedTermIds +
                ", password=****" +
                ", confirmPassword=****" +
                "]";
    }
}
