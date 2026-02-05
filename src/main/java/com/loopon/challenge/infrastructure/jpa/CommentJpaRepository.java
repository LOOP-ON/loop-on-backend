package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.challenge.id = :challengeId AND c.parent IS NULL")
    Slice<Comment> findWithUserByChallengeId(@Param("challengeId") Long challengeId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.parent.id IN :parentIds")
    List<Comment> findAllWithUserByParentIdIn(@Param("parentIds") List<Long> parentIds);
}
