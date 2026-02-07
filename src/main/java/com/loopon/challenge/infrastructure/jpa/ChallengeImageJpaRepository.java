package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.ChallengeImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeImageJpaRepository extends JpaRepository<ChallengeImage, Long> {
    List<ChallengeImage> findAllByChallengeId(Long challengeId);

    @Query("""
            SELECT ci FROM ChallengeImage ci
                JOIN FETCH ci.challenge
                WHERE ci.challenge.user.id = :userId
                AND ci.displayOrder = 0
            """)
    Page<ChallengeImage> findThumbnailsByUserId(@Param("userId") Long userId, Pageable pageable);
           
    void deleteAllByChallengeId(Long challengeId);
}
