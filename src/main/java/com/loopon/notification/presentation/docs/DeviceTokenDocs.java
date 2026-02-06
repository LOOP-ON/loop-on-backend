package com.loopon.notification.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.notification.application.dto.response.DeviceTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "06. 디바이스 토큰", description = "알림 서비스 첫 이용 시 필요한 디바이스 토큰 API")
public interface DeviceTokenDocs {
    @Operation(summary = "새로운 디바이스 토큰 등록", description = "알림 권한 동의 시 새로운 디바이스 토큰을 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<DeviceTokenResponse>> saveDeviceToken(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody DeviceTokenRequest deviceTokenRequest);

    @Operation(summary = "디바이스 토큰 삭제", description = "기존에 등록된 디바이스 토큰을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<String>> deleteDeviceToken(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody DeviceTokenRequest deviceTokenRequest);
}
