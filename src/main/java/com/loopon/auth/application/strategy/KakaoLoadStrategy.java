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

        String email = response.kakaoAccount() != null
                ? response.kakaoAccount().email()
                : null;
        String nickname = (response.kakaoAccount() != null && response.kakaoAccount().profile() != null)
                ? response.kakaoAccount().profile().nickname()
                : null;
        String profileImageUrl = (response.kakaoAccount() != null && response.kakaoAccount().profile() != null)
                ? response.kakaoAccount().profile().profileImageUrl()
                : null;
        
        return new SocialInfoResponse(
                String.valueOf(response.id()),
                email,
                nickname,
                profileImageUrl
        );
    }
}
