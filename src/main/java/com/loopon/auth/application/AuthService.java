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
import com.loopon.user.domain.UserJpaEntity;
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

    private static final int MAX_DB_NICKNAME_LENGTH = 30;
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    private static final String NICKNAME_DELIMITER = "#";

    @Transactional
    public AuthResult login(LoginRequest request) {
        UserJpaEntity userJpaEntity = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), userJpaEntity.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return processLoginSuccess(userJpaEntity);
    }

    @Transactional
    public AuthResult loginSocial(UserProvider provider, String accessToken) {
        SocialLoadStrategy strategy = socialLoadStrategies.stream()
                .filter(s -> s.support(provider))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PROVIDER));

        SocialInfoResponse socialInfo = strategy.loadSocialInfo(accessToken);

        UserJpaEntity userJpaEntity = userRepository.findBySocialIdAndProvider(socialInfo.id(), provider)
                .orElseGet(() -> registerSocialUser(socialInfo, provider));

        return processLoginSuccess(userJpaEntity);
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

    private UserJpaEntity registerSocialUser(SocialInfoResponse info, UserProvider provider) {
        String nickname = info.nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = "LOOP:ON_user";
        }

        String tempNickname;

        int allowedBaseLength = MAX_DB_NICKNAME_LENGTH - NICKNAME_DELIMITER.length() - RANDOM_SUFFIX_LENGTH;

        do {
            String randomSuffix = UUID.randomUUID().toString().substring(0, RANDOM_SUFFIX_LENGTH);

            String safeBaseName = (nickname.length() > allowedBaseLength)
                    ? nickname.substring(0, allowedBaseLength)
                    : nickname;

            tempNickname = safeBaseName + NICKNAME_DELIMITER + randomSuffix;

        } while (userRepository.existsByNickname(tempNickname));

        UserJpaEntity newUserJpaEntity = UserJpaEntity.createSocialUser(
                info.id(),
                provider,
                info.email(),
                tempNickname,
                info.profileImage()
        );

        userRepository.save(newUserJpaEntity);

        return newUserJpaEntity;
    }

    private AuthResult processLoginSuccess(UserJpaEntity userJpaEntity) {
        String accessToken = jwtTokenProvider.createAccessToken(userJpaEntity.getId(), userJpaEntity.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(userJpaEntity.getUserRole())));
        String refreshToken = jwtTokenProvider.createRefreshToken(userJpaEntity.getEmail());

        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(userJpaEntity.getEmail())
                .userId(userJpaEntity.getId())
                .role(userJpaEntity.getUserRole())
                .token(refreshToken)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return AuthResult.of(accessToken, refreshToken);
    }
}
