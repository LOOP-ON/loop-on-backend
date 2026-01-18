package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeImageJpaRepository extends JpaRepository<ChallengeImage, Long> {
    List<ChallengeImage> findAllImageByChallengeId(Long challengeId);
}
