package com.loopon.challenge.infrastructure.jpa;

import com.loopon.challenge.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    Boolean existsByIdAndUserId(Long commentId, Long userId);
}
