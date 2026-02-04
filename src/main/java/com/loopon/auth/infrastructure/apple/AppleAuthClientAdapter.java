package com.loopon.auth.infrastructure.apple;

import com.loopon.auth.infrastructure.apple.dto.AppleTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

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

        return restClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(AppleTokenResponse.class);
    }
}
