package com.loopon.notification.application.dto.response;

//내부적으로 사용하는 API 응답
public record APNsSendResponse(
        boolean success,
        int status,
        String apnsId,
        String reason
) {
    public static APNsSendResponse ok(String apnsId) {
        return new APNsSendResponse(true, 200, apnsId, null);
    }

    public static APNsSendResponse fail(int status, String apnsId, String reason) {
        return new APNsSendResponse(false, status, apnsId, reason);
    }
}