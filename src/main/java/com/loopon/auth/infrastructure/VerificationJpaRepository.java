package com.loopon.auth.infrastructure;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationJpaRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findTopByTargetAndPurposeOrderByCreatedAtDesc(String target, VerificationPurpose purpose);
}
