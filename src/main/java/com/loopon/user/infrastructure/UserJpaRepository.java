package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);
    @Query("""
select u
from User u
where u.id <> :me
  and lower(u.nickname) like lower(concat('%', :query, '%'))
""")
    Page<User> searchByNickname(@Param("me") Long me, @Param("query") String query, Pageable pageable);
}

