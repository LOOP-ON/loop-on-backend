package com.loopon.auth.application.strategy;

import com.loopon.auth.application.dto.response.SocialInfoResponse;
import com.loopon.auth.infrastructure.apple.AppleAuthClientAdapter;
import com.loopon.auth.infrastructure.apple.dto.AppleTokenResponse;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleLoadStrategy implements SocialLoadStrategy {
    private final AppleAuthClientAdapter appleAuthClient;
    private final ObjectMapper objectMapper;

    private static final String SUBJECT = "sub";
    private static final String EMAIL = "email";
    private static final String APPLE_USER_PREFIX = "apple_user_";
    private static final String AT_SIGN = "@";

    @Override
    public boolean support(UserProvider provider) {
        return provider == UserProvider.APPLE;
    }

    @Override
    public SocialInfoResponse loadSocialInfo(String authorizationCode) {
        AppleTokenResponse tokenResponse = appleAuthClient.getTokens(authorizationCode);

        Map<String, Object> claims = parseIdToken(tokenResponse.id_token());

        String socialId = (String) claims.get(SUBJECT);

        if (socialId == null || socialId.isBlank()) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }

        String email = (String) claims.get(EMAIL);

        String nicknameCandidate = APPLE_USER_PREFIX;

        if (email != null && email.contains(AT_SIGN)) {
            nicknameCandidate = email.split(AT_SIGN)[0];
        }

        return new SocialInfoResponse(
                socialId,
                email,
                nicknameCandidate,
                null
        );
    }

    private Map<String, Object> parseIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse Apple ID token: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
