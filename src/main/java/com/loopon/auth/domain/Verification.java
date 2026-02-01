package com.loopon.auth.domain;

import com.loopon.global.domain.BaseTimeEntity;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Verification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationPurpose purpose;

    @Column(nullable = false, length = 100)
    private String target;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Column(nullable = false)
    private Integer attemptCount;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public static Verification of(String email, String code, VerificationPurpose purpose, LocalDateTime expiresAt) {
        return Verification.builder()
                .target(email)
                .code(code)
                .channel(VerificationChannel.EMAIL)
                .purpose(purpose)
                .status(VerificationStatus.PENDING)
                .attemptCount(0)
                .expiresAt(expiresAt)
                .build();
    }

    public void verify(String inputCode, LocalDateTime currentDateTime) {
        if (this.status != VerificationStatus.PENDING) {
            throw new BusinessException(ErrorCode.VERIFICATION_ALREADY_COMPLETED);
        }

        if (currentDateTime.isAfter(this.expiresAt)) {
            this.status = VerificationStatus.EXPIRED;
            throw new BusinessException(ErrorCode.VERIFICATION_EXPIRED);
        }

        if (!this.code.equals(inputCode)) {
            this.attemptCount++;
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        this.status = VerificationStatus.VERIFIED;
    }

    public void markAsUsed() {
        if (this.status != VerificationStatus.VERIFIED) {
            throw new BusinessException(ErrorCode.VERIFICATION_NOT_VERIFIED);
        }
        this.status = VerificationStatus.USED;
    }
}
