package com.loopon.notification.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "05. 알림(Notification)", description = "백엔드 알림 서비스(백엔드에서만 사용합니다)")
public interface NotificationDocs {
    @Operation(summary = "푸시 알림을 보냅니다.", description = "푸시 알림이 필요한 api 내부에서 사용됩니다.")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Void>> sendPush();
}
