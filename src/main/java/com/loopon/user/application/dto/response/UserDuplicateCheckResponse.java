package com.loopon.user.application.dto.response;

public record UserDuplicateCheckResponse(
        boolean isAvailable,
        String message
) {

    public static UserDuplicateCheckResponse of(boolean isAvailable) {
        return new UserDuplicateCheckResponse(
                isAvailable,
                isAvailable ? "사용 가능합니다." : "이미 사용 중입니다."
        );
    }
}
