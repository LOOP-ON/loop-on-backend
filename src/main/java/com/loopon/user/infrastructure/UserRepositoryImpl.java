package com.loopon.user.infrastructure;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Override
    public User findById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("UserRepositoryImpl.findById - 사용자 없음(id: {})", id);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Override
    public Page<User> searchByNickname(Long me, String q, Pageable pageable) {
        return userJpaRepository.searchByNickname(me, q, pageable);
    }
}
