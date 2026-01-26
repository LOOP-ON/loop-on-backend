package com.loopon.auth.infrastructure.kakao;

import com.loopon.auth.infrastructure.kakao.dto.KakaoUserResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetExchange("/v2/user/me")
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String accessToken);
}
