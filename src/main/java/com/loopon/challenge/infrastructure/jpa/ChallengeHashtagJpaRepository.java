package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeHashtagJpaRepository extends JpaRepository<ChallengeHashtag, Long> {
}
