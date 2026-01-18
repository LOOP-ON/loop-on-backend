package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeHashtagJpaRepository extends JpaRepository<ChallengeHashtag, Long> {
    List<ChallengeHashtag> findAllChallengeHashtagByChallengeId(Long challengeId);
}
