package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
            SELECT u FROM User u
                WHERE u.socialId = :id AND u.provider = :provider
            """)
    Optional<User> findBySocialIdAndProvider(@Param("id") String id, @Param("provider") UserProvider provider);

    Optional<User> findByNickname(@Param("nickname") String nickname);
}
