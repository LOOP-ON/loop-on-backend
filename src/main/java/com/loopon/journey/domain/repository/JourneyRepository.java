package com.loopon.journey.domain.repository;

import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findByUserAndStatus(User user, JourneyStatus status);

    List<Journey> findByUser(User user);

    @Query("select coalesce(max(j.journeyOrder), 0) from Journey j where j.user.id = :userId")
    Integer findMaxJourneyOrderByUserId(@Param("userId") Long userId);
}
