package com.loopon.user.domain.repository;

import com.loopon.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    Long save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User findByEmail(String email);

    User findById(Long id);

    Page<User> searchByNickname(Long me, String query, Pageable pageable);
}