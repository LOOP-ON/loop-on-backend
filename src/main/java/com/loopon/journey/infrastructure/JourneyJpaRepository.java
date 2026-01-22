package com.loopon.journey.infrastructure;

import com.loopon.journey.domain.Journey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JourneyJpaRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findById(Long journeyId);

}
