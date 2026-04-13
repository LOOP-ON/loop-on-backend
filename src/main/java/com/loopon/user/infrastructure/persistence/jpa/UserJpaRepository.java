package com.loopon.user.infrastructure.persistence.jpa;

import com.loopon.user.domain.enums.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    Optional<UserJpaEntity> findByEmail(@Param("email") String email);

    @Query("""
            SELECT u FROM UserJpaEntity u
                WHERE u.socialId = :id AND u.provider = :provider
            """)
    Optional<UserJpaEntity> findBySocialIdAndProvider(@Param("id") String id, @Param("provider") UserProvider provider);

    Optional<UserJpaEntity> findByNickname(@Param("nickname") String nickname);
}
