package com.loopon.journey.infrastructure;

import com.loopon.Routine.domain.Routine;
import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JourneyJpaRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findById(Long journeyId);
    boolean existsByRoutineAndProgressDate(Routine routine, LocalDate progressDate);
    Optional<Journey> findByUserAndStatus(User user, JourneyStatus status);

}
