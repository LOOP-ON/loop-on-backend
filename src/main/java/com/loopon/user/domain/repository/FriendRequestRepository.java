package com.loopon.user.domain.repository;

import com.loopon.user.application.dto.response.FriendSearchResponse;
import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<Friend, Long> {

    Page<Friend> findByReceiverIdAndStatusOrderByUpdatedAtDesc(
            Long me,
            FriendStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"receiver", "requester"})
    Optional<Friend> findByRequesterIdAndReceiverIdAndStatus(
            Long requesterId,
            Long receiverId,
            FriendStatus status
    );

    @Query("""
            SELECT f.requester.id
            FROM Friend f
            WHERE f.status = :status
              AND f.receiver.id = :me
            ORDER BY f.updatedAt DESC
            """)
    List<Long> getAllRequesterIdByStatus(
            @Param("me") Long me,
            @Param("status") FriendStatus status
    );
    @Query("""
    SELECT NEW com.loopon.user.application.dto.response.FriendSearchResponse(
        u.nickname,
        u.bio,
        coalesce(f.status, com.loopon.user.domain.FriendStatus.NOT_FRIENDS),
        u.profileImageUrl,
        u.id
    )
    FROM User u
    LEFT JOIN Friend f
        ON (
            (f.requester.id = :me AND f.receiver.id = u.id)
            OR
            (f.receiver.id = :me AND f.requester.id = u.id)
        )
    WHERE u.id <> :me
      AND LOWER(u.nickname) LIKE LOWER(concat('%', :query, '%'))
""")
    Page<FriendSearchResponse> searchByNickname(
            @Param("me") Long me,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
            SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END
            FROM Friend f
            WHERE f.status = :status
              AND ((f.requester.id = :userId1 AND f.receiver.id = :userId2)
                OR (f.requester.id = :userId2 AND f.receiver.id = :userId1))
            """)
    boolean existsFriendship(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("status") FriendStatus status
    );

    List<Friend> findAllByReceiverIdAndStatus(Long receiverId, FriendStatus status);
}