package com.loopon.notification.domain;

import com.loopon.global.domain.BaseTimeEntity;
import com.loopon.global.domain.EnvironmentType;
import com.loopon.notification.application.dto.request.DeviceTokenRequest;
import com.loopon.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_tokens", uniqueConstraints = {@UniqueConstraint(columnNames = {"token", "environment_type"}),})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DeviceToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //한 사용자에 대해 여러 기기 등록 가능(1:N 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    private EnvironmentType environmentType; // DEV, PROD

    private LocalDateTime updatedAt;

    public static DeviceToken create(@NotNull User me, @NotNull DeviceTokenRequest deviceTokenRequest) {
        return DeviceToken.builder()
                .user(me)
                .token(deviceTokenRequest.deviceToken())
                .environmentType(deviceTokenRequest.environmentType())
                .build();
    }

    public void refresh(User user, @NotNull String token) {
        this.user = user;
        this.token = token;
    }
}
