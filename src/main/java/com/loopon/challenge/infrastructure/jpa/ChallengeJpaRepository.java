package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Challenge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, Long> {

    Boolean existsByJourneyId(Long journeyId);

    void deleteAllByExpeditionId(Long expeditionId);

    @Query("SELECT DISTINCT c FROM Challenge c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.journey j " +
            "WHERE c.expedition.id = :expeditionId")
    Slice<Challenge> findAllWithJourneyAndUserByExpeditionId(
            @Param("expeditionId") Long expeditionId,
            Pageable pageable
    );


    @Query("SELECT c FROM Challenge c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.journey j " +
            "WHERE c.createdAt >= :threeDaysAgo " +
            "AND c.user.visibility = 'PUBLIC' " +
            "AND c.user.id != :userId " +
            "ORDER BY (c.likeCount * 2 + c.commentCount * 5) DESC, c.createdAt DESC")
    Slice<Challenge> findTrendingChallenges(
            @Param("threeDaysAgo") LocalDateTime threeDaysAgo,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT c FROM Challenge c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.journey j " +
            "WHERE c.user.id IN :friendsIds " +
            "AND (COALESCE(:excludeIds, NULL) IS NULL OR c.id NOT IN :excludeIds) " +
            "AND c.user.visibility != 'PRIVATE' " +
            "ORDER BY c.createdAt DESC")
    Slice<Challenge> findFriendsChallenges(
            @Param("friendsIds") List<Long> friendsIds,
            @Param("excludeIds") List<Long> excludeIds,
            Pageable pageable
    );

    @Query("SELECT DISTINCT c FROM Challenge c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.journey j " +
            "WHERE u.id = :userId")
    Slice<Challenge> findAllWithJourneyAndUserByUserId(Long userId, Pageable pageable);
}
