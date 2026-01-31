package com.loopon.auth.infrastructure;

import com.loopon.auth.domain.Verification;
import com.loopon.auth.domain.VerificationPurpose;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerificationJpaRepository extends JpaRepository<Verification, Long> {

    @Query("select v from Verification v where v.target = :target and v.purpose = :purpose order by v.createdAt desc")
    List<Verification> findLatest(@Param("target") String target, @Param("purpose") VerificationPurpose purpose, Pageable pageable);
}
