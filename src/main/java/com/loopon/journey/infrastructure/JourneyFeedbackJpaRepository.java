package com.loopon.journey.infrastructure;

import com.loopon.journey.domain.JourneyFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JourneyFeedbackJpaRepository extends JpaRepository<JourneyFeedback, Long> {

    Optional<JourneyFeedback> findByJourneyId(Long journeyId);
}
