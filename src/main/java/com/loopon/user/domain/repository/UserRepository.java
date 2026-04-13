package com.loopon.user.domain.repository;

import com.loopon.user.infrastructure.persistence.jpa.UserJpaEntity;
import com.loopon.user.domain.enums.UserProvider;

import java.util.Optional;

public interface UserRepository {

    Long save(UserJpaEntity userJpaEntity);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findById(Long id);

    Optional<UserJpaEntity> findBySocialIdAndProvider(String id, UserProvider provider);

    Optional<UserJpaEntity> findByNickname(String nickname);
}