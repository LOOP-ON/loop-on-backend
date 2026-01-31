package com.loopon.notification.infrastructure.apns;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopon.notification.application.dto.response.APNsSendResponse;
import com.loopon.notification.domain.service.DeviceTokenInvalidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<DeviceTokenInvalidator> invalidatorProvider;
    @Value("${apns.topic}")
    private String topic;

    public CompletableFuture<APNsSendResponse> send(String deviceToken, String title, String body, Map<String, String> data) {
        CompletableFuture<APNsSendResponse> future = new CompletableFuture<>();
        final String masked = maskToken(deviceToken);
        try {
            String payload = buildPayload(title, body, data);
            String authToken = tokenProvider.getToken();

            apnsWebClient.post()
                    .uri("/3/device/" + deviceToken)
                    .header(HttpHeaders.AUTHORIZATION, "bearer " + authToken)
                    .header("apns-topic", topic)
                    .header("apns-push-type", "alert")
                    .header("apns-priority", "10")
                    .header("apns-expiration", String.valueOf(System.currentTimeMillis() / 1000 + 60))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .exchangeToMono(resp -> {
                        int status = resp.statusCode().value();
                        String apnsId = resp.headers().header("apns-id").stream().findFirst().orElse(null);

                        // 성공(일반적으로 200)
                        if (status >= 200 && status < 300) {
                            return resp.toBodilessEntity()
                                    .thenReturn(APNsSendResponse.ok(apnsId));
                        }

                        // 실패: APNs는 보통 body에 {"reason": "..."}를 줌
                        return resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(bodyStr -> {
                                    String reason = extractReason(bodyStr);
                                    return APNsSendResponse.fail(status, apnsId, reason);
                                });
                    })
                    .subscribe(
                            result -> {
                                if (result.success()) {
                                    log.info("APNs 전송 성공 token={}, apnsId={}", masked, result.apnsId());
                                } else {
                                    log.warn("APNs 전송 실패 token={}, status={}, apnsId={}, reason={}",
                                            masked, result.status(), result.apnsId(), result.reason());

                                    // 대표 처리: 410 Gone = token invalid/unregistered
                                    if (result.status() == 410) {
                                        DeviceTokenInvalidator invalidator = invalidatorProvider.getIfAvailable();
                                        if (invalidator != null) {
                                            invalidator.invalidate(deviceToken, result.reason());
                                        } else {
                                            log.warn("DeviceTokenInvalidator 미구현: token={} reason={}", masked, result.reason());
                                        }
                                    }
                                }
                                future.complete(result);
                            },
                            error -> {
                                // 네트워크/타임아웃/SSL/HTTP2 등 클라이언트 레벨 에러
                                log.error("APNs 호출 자체 실패 token={}, error={}", masked, error.toString());
                                future.complete(APNsSendResponse.fail(-1, null, error.getClass().getSimpleName()));
                            }
                    );

        } catch (Exception e) {
            log.error("APNs 전송 중 코드 레벨 오류 token={}", masked, e);
            future.complete(APNsSendResponse.fail(-1, null, "Exception:" + e.getClass().getSimpleName()));
        }

        return future;
    }

    private String extractReason(String body) {
        if (body == null || body.isBlank()) return null;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode reason = node.get("reason");
            return reason != null && !reason.isNull() ? reason.asText() : null;
        } catch (Exception ignore) {
            // body가 JSON이 아닐 수도 있으니 원문 일부라도 남김
            return body.length() <= 200 ? body : body.substring(0, 200);
        }
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