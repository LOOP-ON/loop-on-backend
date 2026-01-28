package com.loopon.setting.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.setting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.setting.application.dto.response.NotificationSettingResponse;
import com.loopon.setting.domain.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/notification-settings")
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    public ResponseEntity<CommonResponse<NotificationSettingResponse>> getNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        NotificationSettingResponse res = notificationSettingService.getNotificationSetting(principalDetails.getUserId());
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @PatchMapping
    public ResponseEntity<CommonResponse<NotificationSettingResponse>> patchNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody NotificationSettingPatchRequest req) {
        NotificationSettingResponse res = notificationSettingService.patchNotificationSetting(principalDetails.getUserId(), req);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }
}