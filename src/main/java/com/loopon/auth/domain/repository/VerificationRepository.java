package com.loopon.auth.domain.repository;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VerificationRepository {

    Optional<Verification> findLatest(String target, VerificationPurpose purpose, Pageable pageable);
}
