package com.loopon.user.application.dto.request;

import com.loopon.user.application.dto.command.UpdateProfileCommand;
import com.loopon.user.domain.UserVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 수정 요청 DTO")
public record UpdateProfileRequest(
        @Schema(description = "변경할 닉네임 (중복 불가)", example = "NewLoopon")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname,

        @Schema(description = "한 줄 소개 (Bio)", example = "백엔드 개발자입니다.")
        @Size(max = 100, message = "한 줄 소개는 100자를 넘을 수 없습니다.")
        String bio,

        @Schema(description = "상태 메시지", example = "코딩 중...")
        @Size(max = 100, message = "상태 메시지는 100자를 넘을 수 없습니다.")
        String statusMessage,

        @Schema(description = "프로필 이미지 URL (프로필 이미지 업로드 API 호출 후 반환된 URL)", example = "https://s3.../image.jpg")
        String profileImageUrl,

        @Schema(description = "프로필 공개 범위 (PUBLIC, PRIVATE)", example = "PUBLIC")
        @NotNull(message = "프로필 공개 범위는 필수입니다.")
        UserVisibility visibility
) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(
                this.nickname,
                this.bio,
                this.statusMessage,
                this.profileImageUrl,
                this.visibility
        );
    }
}
