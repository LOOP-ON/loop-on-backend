package com.loopon.notificationsetting.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.notificationsetting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;
import com.loopon.notificationsetting.domain.service.NotificationSettingService;
import com.loopon.notificationsetting.presentation.docs.NotificationSettingDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/notification-settings")
public class NotificationSettingController implements NotificationSettingDocs {

    private final NotificationSettingService notificationSettingService;

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<NotificationSettingResponse>> getNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        NotificationSettingResponse res = notificationSettingService.getNotificationSetting(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @Override
    @PatchMapping
    public ResponseEntity<CommonResponse<NotificationSettingResponse>> patchNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody NotificationSettingPatchRequest req) {
        NotificationSettingResponse res = notificationSettingService.patchNotificationSetting(principalDetails.getUserId(), req);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
}
