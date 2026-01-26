package com.loopon.auth.infrastructure.kakao;

import com.loopon.auth.infrastructure.kakao.dto.KakaoUserResponse;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoRestClientAdapter {
    private final RestClient kakaoRestClient;

    public KakaoUserResponse getUserInfo(String accessToken) {
        try {
            return kakaoRestClient.get()
                    .uri("/v2/user/me")
                    .header("Authorization", accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new BusinessException(ErrorCode.EXTERNAL_SERVER_ERROR);
                    })
                    .body(KakaoUserResponse.class);

        } catch (Exception e) {
            log.error("Kakao API Call Failed: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
