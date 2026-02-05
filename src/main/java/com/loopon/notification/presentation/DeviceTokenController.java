package com.loopon.notification.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.notification.application.dto.response.DeviceTokenResponse;
import com.loopon.notification.domain.service.DeviceTokenService;
import com.loopon.notification.presentation.docs.DeviceTokenDocs;
import com.loopon.notification.presentation.docs.NotificationDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deviceToken")
public class DeviceTokenController implements DeviceTokenDocs {
    private final DeviceTokenService deviceTokenService;

    @PostMapping()
    public ResponseEntity<CommonResponse<DeviceTokenResponse>> saveDeviceToken(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody DeviceTokenRequest deviceTokenRequest) {
        Long me = principalDetails.getUserId();
        DeviceTokenResponse res = deviceTokenService.saveDeviceToken(me, deviceTokenRequest);
        return ResponseEntity.ok(CommonResponse.onSuccess(res));
    }

    @DeleteMapping()
    public ResponseEntity<CommonResponse<String>> deleteDeviceToken(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody DeviceTokenRequest deviceTokenRequest) {
        Long me = principalDetails.getUserId();
        deviceTokenService.deleteDeviceToken(me, deviceTokenRequest);
        return ResponseEntity.ok(CommonResponse.onSuccess("디바이스 토큰이 삭제되었습니다."));
    }
}
