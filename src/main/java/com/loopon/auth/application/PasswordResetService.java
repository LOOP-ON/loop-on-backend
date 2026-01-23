package com.loopon.auth.application;

import com.loopon.auth.application.dto.request.PasswordEmailRequest;
import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.application.dto.request.PasswordVerifyRequest;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.mail.MailService;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final RedisAuthAdapter redisAuthAdapter;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public void sendAuthCode(PasswordEmailRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        String authCode = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));

        redisAuthAdapter.saveAuthCode(request.email(), authCode);
        mailService.sendAuthCode(request.email(), authCode);
    }

    public String verifyAuthCode(PasswordVerifyRequest request) {
        String storedCode = redisAuthAdapter.getAuthCode(request.email());

        if (storedCode == null || !storedCode.equals(request.code())) {
            throw new BusinessException(ErrorCode.AUTH_CODE_INVALID);
        }

        String resetToken = UUID.randomUUID().toString();
        redisAuthAdapter.saveResetToken(request.email(), resetToken);

        redisAuthAdapter.deleteAuthCode(request.email());

        return resetToken;
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        String storedToken = redisAuthAdapter.getResetToken(request.email());
        if (storedToken == null || !storedToken.equals(request.resetToken())) {
            throw new BusinessException(ErrorCode.RESET_TOKEN_INVALID);
        }

        User user = userRepository.findByEmail(request.email());

        user.updatePassword(passwordEncoder.encode(request.newPassword()));

         refreshTokenRepository.deleteById(request.email());
    }
}
