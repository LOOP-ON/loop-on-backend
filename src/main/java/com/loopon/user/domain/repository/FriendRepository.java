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

    Long countByReceiver_IdAndStatus(Long me, FriendStatus friendStatus);
}
