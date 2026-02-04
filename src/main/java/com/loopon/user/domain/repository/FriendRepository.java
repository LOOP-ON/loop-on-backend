package com.loopon.user.domain.repository;

import com.loopon.user.domain.Friend;
import com.loopon.user.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
        select f
        from Friend f
        join fetch f.requester
        join fetch f.receiver
        where f.status = :status
          and (f.requester.id = :me or f.receiver.id = :me)
        """)
    List<Friend> findAcceptedFriendsByUserId(
            @Param("me") Long me,
            @Param("status") FriendStatus status
    );
    @Query("""
        select (count(f) > 0)
        from Friend f
        where f.id = :friendId
          and (f.requester.id = :me or f.receiver.id = :me)
        """)
    boolean existsByIdAndUserId(
            @Param("friendId") Long friendId,
            @Param("me") Long me
    );


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
