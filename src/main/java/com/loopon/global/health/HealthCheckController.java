package com.loopon.global.health;

import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Hidden
@RequiredArgsConstructor
public class HealthCheckController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/")
    public ResponseEntity<CommonResponse<Map<String, String>>> systemStatus() {
        Map<String, String> status = Map.of(
                "status", "UP",
                "profile", activeProfile,
                "serverTime", LocalDateTime.now().toString(),
                "message", "LoopOn API Server is running!"
        );

        return ResponseEntity.ok(CommonResponse.onSuccess(status));
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}