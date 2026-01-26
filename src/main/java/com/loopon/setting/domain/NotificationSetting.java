package com.loopon.setting.domain;

import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_setting_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 전체 알림
    @Column(nullable = false)
    private boolean allEnabled;

    // 루틴 관련
    @Column(nullable = false)
    private boolean routineEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertMode routineAuthMode;

    @Column(nullable = false)
    private boolean unfinishedReminderEnabled;

    // 여정 관련
    @Column(nullable = false)
    private boolean dayEndEnabled;

    @Column
    private java.time.LocalTime dayEndTime;

    @Column(nullable = false)
    private boolean journeyCompleteEnabled;

    // 시스템 알림
    @Column(nullable = false)
    private boolean friendRequestEnabled;

    @Column(nullable = false)
    private boolean likeEnabled;

    @Column(nullable = false)
    private boolean commentEnabled;

    @Column(nullable = false)
    private boolean noticeUpdateEnabled;

    @Column(nullable = false)
    private boolean marketingEnabled;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

