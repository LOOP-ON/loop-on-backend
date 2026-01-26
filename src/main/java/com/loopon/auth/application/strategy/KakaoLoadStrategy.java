package com.loopon.auth.application.strategy;

import com.loopon.auth.application.dto.response.SocialInfoResponse;
import com.loopon.auth.infrastructure.kakao.KakaoRestClientAdapter;
import com.loopon.auth.infrastructure.kakao.dto.KakaoUserResponse;
import com.loopon.user.domain.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoLoadStrategy implements SocialLoadStrategy {
    private final KakaoRestClientAdapter kakaoAdapter;

    @Override
    public boolean support(UserProvider provider) {
        return provider == UserProvider.KAKAO;
    }

    @Override
    public SocialInfoResponse loadSocialInfo(String accessToken) {
        KakaoUserResponse response = kakaoAdapter.getUserInfo(accessToken);

        return new SocialInfoResponse(
                String.valueOf(response.id()),
                response.kakaoAccount().email(),
                response.kakaoAccount().profile().nickname(),
                response.kakaoAccount().profile().profileImageUrl()
        );
    }
}
