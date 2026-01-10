package com.loopon.domain.auth.entity;

import com.loopon.domain.auth.enums.VerificationChannel;
import com.loopon.domain.auth.enums.VerificationPurpose;
import com.loopon.domain.auth.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verification {

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
