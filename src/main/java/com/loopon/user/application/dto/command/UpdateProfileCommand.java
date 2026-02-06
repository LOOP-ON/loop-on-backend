package com.loopon.user.application.dto.command;

import com.loopon.user.domain.UserVisibility;

public record UpdateProfileCommand(
        String nickname,
        String bio,
        String statusMessage,
        String profileImageUrl,
        UserVisibility visibility
) {
}
