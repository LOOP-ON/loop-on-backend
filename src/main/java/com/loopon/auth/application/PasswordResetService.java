package com.loopon.auth.application;

import com.loopon.auth.application.dto.request.PasswordResetRequest;
import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.auth.domain.repository.VerificationRepository;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final RedisAuthAdapter redisAuthAdapter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        String storedToken = redisAuthAdapter.getResetToken(request.email());

        if (storedToken == null || !storedToken.equals(request.resetToken())) {
            log.warn("[PasswordReset] Invalid or expired token for email: {}", request.email());
            throw new BusinessException(ErrorCode.INVALID_RESET_TOKEN);
        }
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(request.newPassword()));

        markVerificationAsUsed(request.email());

        log.info("[PasswordReset] Password changed successfully for user: {}", user.getId());
    }

    private void markVerificationAsUsed(String email) {
        verificationRepository.findLatest(
                email,
                VerificationPurpose.PASSWORD_RESET,
                PageRequest.of(0, 1)
        ).stream().findFirst().ifPresent(verification -> {
            try {
                verification.markAsUsed();
            } catch (Exception e) {
                log.warn("[PasswordReset] Failed to mark verification as USED: {}", e.getMessage());
            }
        });
    }
}
