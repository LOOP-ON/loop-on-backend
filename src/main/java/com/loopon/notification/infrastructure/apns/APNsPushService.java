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
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class APNsPushService {

    private final RestClient apnsRestClient;
    private final APNsTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<DeviceTokenInvalidator> invalidatorProvider;
    @Value("${apple.client-id}")
    private String topic;

    public CompletableFuture<APNsSendResponse> send(String deviceToken, String title, String body, Map<String, String> data) {
        final String masked = maskToken(deviceToken);
        return CompletableFuture.supplyAsync(() -> {
            try {
                String payload = buildPayload(title, body, data);
                String authToken = tokenProvider.getToken();

                APNsSendResponse result = apnsRestClient.post()
                        .uri("/3/device/" + deviceToken)
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + authToken)
                        .header("apns-topic", topic)
                        .header("apns-push-type", "alert")
                        .header("apns-priority", "10")
                        .header("apns-expiration", String.valueOf(System.currentTimeMillis() / 1000 + 60))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .exchange((req, res) -> {
                            int status = res.getStatusCode().value();
                            String apnsId = res.getHeaders().getFirst("apns-id");

                            if (status >= 200 && status < 300) {
                                return APNsSendResponse.ok(apnsId);
                            }

                            String bodyStr = "";
                            if (res.getBody() != null) {
                                bodyStr = StreamUtils.copyToString(res.getBody(), StandardCharsets.UTF_8);
                            }
                            String reason = extractReason(bodyStr);
                            return APNsSendResponse.fail(status, apnsId, reason);
                        });
                if (result.success()) {
                    log.info("APNs 전송 성공 token={}, apnsId={}", masked, result.apnsId());
                } else {
                    log.warn("APNs 전송 실패 token={}, status={}, apnsId={}, reason={}",
                            masked, result.status(), result.apnsId(), result.reason());

                    if (result.status() == 410) {
                        DeviceTokenInvalidator invalidator = invalidatorProvider.getIfAvailable();
                        if (invalidator != null) invalidator.invalidate(deviceToken, result.reason());
                        else log.warn("DeviceTokenInvalidator 미구현: token={} reason={}", masked, result.reason());
                    }
                }

                return result;

            } catch (Exception e) {
                log.error("APNs 전송 중 코드 레벨 오류 token={}", masked, e);
                return APNsSendResponse.fail(-1, null, "Exception:" + e.getClass().getSimpleName());
            }
        });
    }

    private String extractReason(String body) {
        if (body == null || body.isBlank()) return null;
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode reason = node.get("reason");
            return reason != null && !reason.isNull() ? reason.asText() : null;
        } catch (Exception ignore) {
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
