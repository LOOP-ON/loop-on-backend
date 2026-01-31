package com.loopon.auth.infrastructure;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import com.loopon.auth.domain.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VerificationRepositoryImpl implements VerificationRepository {
    private final VerificationJpaRepository verificationJpaRepository;

    @Override
    public Optional<Verification> findLatest(String target, VerificationPurpose purpose, Pageable pageable) {
        return verificationJpaRepository.findLatest(target, purpose, pageable)
                .stream()
                .findFirst();
    }

    @Override
    public void save(Verification verification) {
        verificationJpaRepository.save(verification);
    }
}
