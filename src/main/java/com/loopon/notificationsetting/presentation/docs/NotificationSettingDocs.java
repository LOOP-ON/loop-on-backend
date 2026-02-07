package com.loopon.notificationsetting.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.notificationsetting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "04. 알림 설정(Notification-Setting)", description = "알림 설정 변경 API")
public interface NotificationSettingDocs {

    @Operation(summary = "알림 설정 정보 가져오기", description = "기존 알림 설정 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<NotificationSettingResponse>> getNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails);

    @Operation(summary = "알림 설정 정보 수정", description = "알림 설정을 변경합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<NotificationSettingResponse>> patchNotificationSetting(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody NotificationSettingPatchRequest req);
}
