package com.loopon.user.domain.repository;

import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
            SELECT f
            FROM Friend f
            JOIN FETCH f.requester
            JOIN FETCH f.receiver
            WHERE f.status = :status
              AND (f.requester.id = :me OR f.receiver.id = :me)
            """)
    List<Friend> findFriendsByUserIdAndStatus(
            @Param("me") Long me,
            @Param("status") FriendStatus status
    );

    @Query("""
        SELECT f
        FROM Friend f
        WHERE f.status = :status
          AND (f.requester.id = :me OR f.receiver.id = :me)
        ORDER BY f.updatedAt DESC
    """)
    Slice<Friend> getFriendsByUserIdAndStatus(
            @Param("me") Long me,
            @Param("status") FriendStatus status,
            Pageable pageable
    );

    Long countByReceiver_IdAndStatus(Long me, FriendStatus friendStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Friend f
                   SET f.status = :to
                 WHERE f.id = :friendId
                   AND (f.requester.id = :me OR f.receiver.id = :me)
                   AND f.status = :from
            """)
    int updateStatusByIdAndParticipantAndStatus(
            @Param("friendId") Long friendId,
            @Param("me") Long me,
            @Param("from") FriendStatus from,
            @Param("to") FriendStatus to
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM Friend f
            WHERE f.id = :friendId
              AND (:me = f.requester.id OR :me = f.receiver.id)
            """)
    int deleteByIdAndParticipant(@Param("friendId") Long friendId, @Param("me") Long me);


    @Query("SELECT COUNT(f) > 0 FROM Friend f " +
            "WHERE f.status = :status " +
            "AND ((f.requester.id = :userAId AND f.receiver.id = :userBId) " +
            "OR (f.requester.id = :userBId AND f.receiver.id = :userAId))")
    boolean existsFriendship(
            @Param("userAId") Long userAId,
            @Param("userBId") Long userBId,
            @Param("status") FriendStatus status
    );
}
