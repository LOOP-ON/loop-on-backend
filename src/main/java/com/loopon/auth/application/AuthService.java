package com.loopon.auth.application;

import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.application.dto.response.AuthResult;
import com.loopon.auth.application.dto.response.SocialInfoResponse;
import com.loopon.auth.application.strategy.SocialLoadStrategy;
import com.loopon.auth.domain.RefreshToken;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.AuthorizationException;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.security.jwt.JwtTokenProvider;
import com.loopon.global.security.jwt.JwtTokenValidator;
import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final PasswordEncoder passwordEncoder;
    private final List<SocialLoadStrategy> socialLoadStrategies;

    @Transactional
    public AuthResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return processLoginSuccess(user);
    }

    @Transactional
    public AuthResult loginSocial(UserProvider provider, String accessToken) {
        SocialLoadStrategy strategy = socialLoadStrategies.stream()
                .filter(s -> s.support(provider))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROVIDER));

        SocialInfoResponse socialInfo = strategy.loadSocialInfo(accessToken);

        User user = userRepository.findBySocialIdAndProvider(socialInfo.id(), provider)
                .orElseGet(() -> registerSocialUser(socialInfo, provider));

        return processLoginSuccess(user);
    }

    @Transactional
    public AuthResult reissueTokens(String refreshToken) {
        jwtTokenValidator.validateToken(refreshToken);

        String email = jwtTokenValidator.getEmailFromRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN));

        RefreshToken savedRefreshToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AuthorizationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new AuthorizationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = savedRefreshToken.getUserId();
        String role = savedRefreshToken.getRole();

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, email, Collections.singletonList(new SimpleGrantedAuthority(role)));
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        RefreshToken newRefreshTokenEntity = savedRefreshToken.rotate(newRefreshToken);
        refreshTokenRepository.save(newRefreshTokenEntity);

        return AuthResult.of(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        try {
            jwtTokenValidator.getEmailFromRefreshToken(refreshToken)
                    .ifPresent(refreshTokenRepository::deleteById);
        } catch (Exception e) {
            if (e instanceof AuthorizationException) {
                return;
            }
        }
    }

    private User registerSocialUser(SocialInfoResponse info, UserProvider provider) {
        String nickname = info.nickname();
        String tempNickname;

        do {
            String randomSuffix = UUID.randomUUID().toString().substring(0, 4);

            if (nickname.length() > 25) {
                nickname = nickname.substring(0, 25);
            }

            tempNickname = nickname + "#" + randomSuffix;

        } while (userRepository.existsByNickname(tempNickname));

        User newUser = User.createSocialUser(
                info.id(),
                provider,
                info.email(),
                tempNickname,
                info.profileImage()
        );

        userRepository.save(newUser);

        return newUser;
    }

    private AuthResult processLoginSuccess(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole())));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(user.getEmail())
                .userId(user.getId())
                .role(user.getUserRole())
                .token(refreshToken)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return AuthResult.of(accessToken, refreshToken);
    }
}
