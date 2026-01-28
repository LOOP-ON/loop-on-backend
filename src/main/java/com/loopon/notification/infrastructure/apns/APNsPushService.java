package com.loopon.notification.infrastructure.apns;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class APNsPushService {

    private final WebClient apnsWebClient;
    private final APNsTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Value("${apns.topic}")
    private String topic;

    public CompletableFuture<Boolean> send(String deviceToken, String title, String body, Map<String, String> data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            String payload = buildPayload(title, body, data);
            String authToken = tokenProvider.generateToken();

            apnsWebClient.post()
                    .uri("/3/device/" + deviceToken)
                    .header(HttpHeaders.AUTHORIZATION, "bearer " + authToken)
                    .header("apns-topic", topic)
                    .header("apns-push-type", "alert")
                    .header("apns-priority", "10")
                    .header("apns-expiration", String.valueOf(System.currentTimeMillis() / 1000 + 60))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                            response -> {
                                log.info("APNs 전송 성공: {}", maskToken(deviceToken));
                                future.complete(true);
                            },
                            error -> {
                                log.error("APNs 전송 실패: {}", error.getMessage());
                                future.complete(false);
                            }
                    );

        } catch (Exception e) {
            log.error("APNs 전송 중 오류 발생", e);
            future.complete(false);
        }

        return future;
    }

    private String buildPayload(String title, String body, Map<String, String> data) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> aps = new HashMap<>();
        Map<String, String> alert = new HashMap<>();

        alert.put("title", title);
        alert.put("body", body);

        aps.put("alert", alert);
        aps.put("sound", "default");
        aps.put("badge", 1);

        payload.put("aps", aps);

        if (data != null && !data.isEmpty()) {
            payload.putAll(data);
        }

        return objectMapper.writeValueAsString(payload);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) return "***";
        return token.substring(0, 8) + "..." + token.substring(token.length() - 8);
    }
}