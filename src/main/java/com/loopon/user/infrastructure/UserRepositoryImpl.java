package com.loopon.user.infrastructure;

import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

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
    public User findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("해당 이메일의 사용자가 존재하지 않습니다.")
                );
    }
}
