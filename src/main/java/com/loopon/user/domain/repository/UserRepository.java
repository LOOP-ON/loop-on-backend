package com.loopon.user.domain.repository;

import com.loopon.user.domain.User;

public interface UserRepository {

    Long save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
