package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("""
            select u from User u
                    where u.socialId = :id and u.provider = :provider
            """)
    Optional<User> findBySocialIdAndProvider(@Param("id") String id, @Param("provider") UserProvider provider);

    Optional<User> findByNickname(String nickname);
}
