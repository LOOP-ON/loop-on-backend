package com.loopon.journey.domain.repository;

import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findById(Long journeyId);
    Optional<Journey> findByUserAndStatus(User user, JourneyStatus status);
    List<Journey> findByUser(User user);
}
