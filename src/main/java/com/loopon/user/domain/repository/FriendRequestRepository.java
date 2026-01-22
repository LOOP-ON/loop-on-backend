package com.loopon.user.domain.repository;

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

    boolean existsByStatusAndRequesterIdAndReceiverId(
            FriendStatus status,
            Long requesterId,
            Long receiverId
    );

    @EntityGraph(attributePaths = {"receiver", "requester"})
    Optional<Friend> findByRequesterIdAndReceiverIdAndStatus(
            Long requesterId,
            Long receiverId,
            FriendStatus status
    );

    @Query("""
    select f.requester.id
    from Friend f
    where f.status = :status
      and f.receiver.id = :me
    order by f.updatedAt desc
    """)
    List<Long> getAllRequesterIdByStatus(
            @Param("me") Long me,
            @Param("status") FriendStatus status
    );
    @Query("""
    SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
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
}