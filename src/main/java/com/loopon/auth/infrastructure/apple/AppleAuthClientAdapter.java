package com.loopon.auth.infrastructure.apple;

import com.loopon.auth.infrastructure.apple.dto.AppleTokenResponse;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleAuthClientAdapter {
    private final RestClient restClient;
    private final AppleClientSecretGenerator secretGenerator;

    @Value("${apple.client-id}")
    private String clientId;

    public AppleTokenResponse getTokens(String authorizationCode) {
        String clientSecret = secretGenerator.createClientSecret();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        try {
            return restClient.post()
                    .uri("https://appleid.apple.com/auth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.error("Apple Client Error: Status Code = {}, Body = {}",
                                response.getStatusCode(), new String(response.getBody().readAllBytes()));
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        log.error("Apple Server Error: Status Code = {}", response.getStatusCode());
                        throw new BusinessException(ErrorCode.EXTERNAL_SERVER_ERROR);
                    })
                    .body(AppleTokenResponse.class);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple API Unhandled Exception: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
