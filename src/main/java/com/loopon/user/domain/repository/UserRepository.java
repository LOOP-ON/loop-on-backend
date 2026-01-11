package com.loopon.user.domain.repository;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
