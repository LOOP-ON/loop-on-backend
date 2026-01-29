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
        String tokenHeader = accessToken.startsWith("Bearer ")
                ? accessToken
                : "Bearer " + accessToken;

        try {
            return kakaoRestClient.get()
                    .uri("/v2/user/me")
                    .header("Authorization", tokenHeader)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.error("Kakao Client Error: Status Code = {}", response.getStatusCode());
                        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        log.error("Kakao Server Error: Status Code = {}", response.getStatusCode());
                        throw new BusinessException(ErrorCode.EXTERNAL_SERVER_ERROR);
                    })
                    .body(KakaoUserResponse.class);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Kakao API Unhandled Exception: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }
    }
}
