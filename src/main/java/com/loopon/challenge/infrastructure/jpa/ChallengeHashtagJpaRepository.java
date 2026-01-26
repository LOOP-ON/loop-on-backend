package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeHashtag;
import com.loopon.challenge.domain.ChallengeHashtagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeHashtagJpaRepository extends JpaRepository<ChallengeHashtag, ChallengeHashtagId> {
    List<ChallengeHashtag> findAllByChallengeId(Long challengeId);

    List<ChallengeHashtag> findAllWithHashtagByChallengeId(Long challengeId);
}
