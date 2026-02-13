package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
                FROM User u
                WHERE u.email = :email
            """)
    boolean existsByEmail(@Param("email") String email);

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
                FROM User u
                WHERE u.nickname = :nickname
            """)
    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("""
            SELECT u FROM User u
                WHERE u.email = :email
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
            SELECT u FROM User u
                WHERE u.socialId = :id AND u.provider = :provider
            """)
    Optional<User> findBySocialIdAndProvider(@Param("id") String id, @Param("provider") UserProvider provider);

    @Query("""
            SELECT u FROM User u
                WHERE u.nickname = :nickname
            """)
    Optional<User> findByNickname(@Param("nickname") String nickname);
}
