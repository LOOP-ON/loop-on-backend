package com.loopon.journey.infrastructure;

import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

public interface JourneyJpaRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findByUserAndStatus(User user, JourneyStatus status);

    @Query("select coalesce(max(j.journeyOrder), 0) from Journey j where j.user.id = :userId")
    Integer findMaxJourneyOrderByUserId(@Param("userId") Long userId);
}

