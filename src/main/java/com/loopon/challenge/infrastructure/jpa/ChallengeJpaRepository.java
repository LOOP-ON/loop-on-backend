package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, Long> {

    boolean existsByJourneyId(Long journeyId);
}
