package com.loopon.journey.infrastructure;

import com.loopon.journey.domain.Journey;
import com.loopon.journey.domain.JourneyCategory;
import com.loopon.journey.domain.JourneyStatus;
import com.loopon.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

public interface JourneyJpaRepository extends JpaRepository<Journey, Long> {

    Optional<Journey> findByUserAndStatus(User user, JourneyStatus status);

    @Query("SELECT COALESCE(MAX(j.journeyOrder), 0) FROM Journey j WHERE j.user = :user")
    Integer findMaxJourneyOrderByUser(@Param("user") User user);

    @Query("SELECT COALESCE(MAX(j.journeyOrder), 0) FROM Journey j WHERE j.user.id = :userId")
    Integer findMaxJourneyOrderByUserId(@Param("userId") Long userId);

    @Query("SELECT j " +
            "FROM Journey j " +
            "WHERE j.id IN (" +
            "    SELECT MAX(j2.id) " +
            "    FROM Journey j2 " +
            "    WHERE j2.user.id = :userId " +
            "    GROUP BY j2.goal, j2.category" +
            ")")
    Slice<Journey> findDistinctJourneyByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT j FROM Journey j " +
            "WHERE j.user.id = :userId " +
            "AND j.goal LIKE %:keyword% " +
            "AND j.category IN :categories " +
            "AND j.id IN (" +
            "    SELECT MAX(j2.id) " +
            "    FROM Journey j2 " +
            "    WHERE j2.user.id = :userId " +
            "    GROUP BY j2.goal, j2.category" +
            ")")
    Slice<Journey> findDistinctJourneyBySearch(
            @Param("keyword") String keyword,
            @Param("categories") List<JourneyCategory> categories,
            @Param("userId") Long userId,
            Pageable pageable
    );

    boolean existsByUserAndStatus(User user, JourneyStatus status);
}
