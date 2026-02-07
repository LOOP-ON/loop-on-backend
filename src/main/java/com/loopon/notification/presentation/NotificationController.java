package com.loopon.notification.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.notification.infrastructure.apns.APNsPushService;
import com.loopon.notification.presentation.docs.NotificationDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController implements NotificationDocs {
    private final APNsPushService apnsPushService;

    @Override
    @PostMapping("/send-push")
    public ResponseEntity<CommonResponse<Void>> sendPush() {
        Map<String, String> data = new HashMap<>();
        data.put("type", "reminder");
        data.put("id", "123");

        apnsPushService.send(
                "디바이스토큰",
                "제목",
                "메시지 내용",
                data
        );
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
