package com.loopon.notification.domain.repository;

import com.loopon.global.domain.EnvironmentType;
import com.loopon.notification.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUserIdAndEnvironmentType(Long userId, EnvironmentType env);

    Optional<DeviceToken> findByTokenAndEnvironmentType(String token, EnvironmentType env);

    Optional<DeviceToken> findByUserIdAndToken(Long userId, String token);

    void deleteByToken(String deviceToken);
}
