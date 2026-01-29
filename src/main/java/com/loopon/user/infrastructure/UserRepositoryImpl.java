package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Long save(User user) {
        User savedUser = userJpaRepository.save(user);
        return savedUser.getId();
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
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Page<User> searchByNickname(Long me, String q, Pageable pageable) {
        return userJpaRepository.searchByNickname(me, q, pageable);
    }

    @Override
    public Optional<User> findBySocialIdAndProvider(String id, UserProvider provider) {
        return userJpaRepository.findBySocialIdAndProvider(id, provider);
    }
}
