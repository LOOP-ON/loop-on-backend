package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeLike;
import com.loopon.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChallengeLikeJpaRepository extends JpaRepository<ChallengeLike, Long> {
    Boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);

    Optional<ChallengeLike> findByUserIdAndChallengeId(Long userId, Long challengeId);

    List<ChallengeLike> user(User user);

    @Query("SELECT l.challenge.id FROM ChallengeLike l " +
            "WHERE l.user.id = :userId AND l.challenge.id IN :challengeIds")
    Set<Long> findLikedChallengeIds(
            @Param("userId") Long userId,
            @Param("challengeIds") List<Long> challengeIds
    );
}
