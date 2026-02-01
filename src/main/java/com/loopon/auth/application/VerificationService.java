package com.loopon.auth.application;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.auth.domain.repository.VerificationRepository;
import com.loopon.auth.infrastructure.RedisAuthAdapter;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.global.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VerificationService {
    private static final SecureRandom secureRandom = new SecureRandom();

    private final VerificationRepository verificationRepository;
    private final EmailService emailService;
    private final RedisAuthAdapter redisAuthAdapter;

    @Transactional
    public void sendCode(String email, VerificationPurpose purpose) {
        if (redisAuthAdapter.isRateLimitExceeded(email)) {
            log.warn("[Verification] Rate limit exceeded for email: {}", email);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        String code = generateCode();

        Verification verification = Verification.of(email, code, purpose, LocalDateTime.now().plusMinutes(5));
        verificationRepository.save(verification);

        emailService.sendVerificationEmail(email, code, purpose);
    }

    @Transactional
    public String verifyCode(String email, String code, VerificationPurpose purpose) {
        Verification verification = verificationRepository.findLatest(
                        email,
                        purpose)
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_NOT_FOUND));

        verification.verify(code, LocalDateTime.now());

        if (purpose == VerificationPurpose.PASSWORD_RESET) {
            String resetToken = UUID.randomUUID().toString();
            redisAuthAdapter.saveResetToken(email, resetToken);
            return resetToken;
        }

        return null;
    }

    private String generateCode() {
        int randomInt = secureRandom.nextInt(10000);
        return String.format("%04d", randomInt);
    }
}
