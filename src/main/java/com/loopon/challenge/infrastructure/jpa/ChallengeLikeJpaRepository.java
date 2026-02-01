package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeLikeJpaRepository extends JpaRepository<ChallengeLike, Long> {
    Boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);
}
