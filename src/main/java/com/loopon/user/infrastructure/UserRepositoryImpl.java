package com.loopon.user.infrastructure;

import com.loopon.user.domain.UserJpaEntity;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Long save(UserJpaEntity userJpaEntity) {
        UserJpaEntity savedUserJpaEntity = userJpaRepository.save(userJpaEntity);
        return savedUserJpaEntity.getId();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<UserJpaEntity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<UserJpaEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<UserJpaEntity> findBySocialIdAndProvider(String id, UserProvider provider) {
        return userJpaRepository.findBySocialIdAndProvider(id, provider);
    }

    @Override
    public Optional<UserJpaEntity> findByNickname(String nickname) {
        return userJpaRepository.findByNickname(nickname);
    }
}
