package com.loopon.auth.application.strategy;

import com.loopon.auth.application.dto.response.SocialInfoResponse;
import com.loopon.user.domain.UserProvider;

public interface SocialLoadStrategy {

    boolean support(UserProvider provider);
    SocialInfoResponse loadSocialInfo(String accessToken);
}
