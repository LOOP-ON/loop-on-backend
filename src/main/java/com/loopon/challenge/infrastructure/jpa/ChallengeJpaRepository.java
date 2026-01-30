package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Challenge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, Long> {

    Boolean existsByJourneyId(Long journeyId);

    void deleteAllByExpeditionId(Long expeditionId);

    Slice<Challenge> findAllWithJourneyAndUserByExpeditionId(Long expeditionId, Pageable pageable);
}
