package com.loopon.setting.domain;

import com.loopon.setting.application.dto.request.NotificationSettingPatchRequest;
import com.loopon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private AlertMode routineAlertMode;

    @Column(nullable = false)
    private boolean unfinishedGoalReminderEnabled;

    @Column(nullable = false)
    private LocalTime unfinishedGoalReminderTime;

    //리마인드 알림 기본 시각 23시
    @PrePersist
    public void prePersist() {
        if (unfinishedGoalReminderTime == null) {
            this.unfinishedGoalReminderTime = LocalTime.of(23, 0);
        }
    }

    // 여정 관련
    @Column(nullable = false)
    private boolean dayEndJourneyReminderEnabled;

    @Column
    private LocalTime dayEndJourneyReminderTime;

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
    private boolean noticeEnabled;

    @Column(nullable = false)
    private boolean marketingEnabled;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void apply(NotificationSettingPatchRequest req) {
        if (req.allEnabled() != null) this.allEnabled = req.allEnabled();

        if (req.routineEnabled() != null) this.routineEnabled = req.routineEnabled();
        if (req.routineAlertMode() != null) this.routineAlertMode = req.routineAlertMode();

        if (req.unfinishedGoalReminderEnabled() != null)
            this.unfinishedGoalReminderEnabled = req.unfinishedGoalReminderEnabled();
        if (req.unfinishedGoalReminderTime() != null)
            this.unfinishedGoalReminderTime = req.unfinishedGoalReminderTime();

        if (req.dayEndJourneyReminderEnabled() != null)
            this.dayEndJourneyReminderEnabled = req.dayEndJourneyReminderEnabled();
        if (req.dayEndJourneyReminderTime() != null) this.dayEndJourneyReminderTime = req.dayEndJourneyReminderTime();

        if (req.journeyCompleteEnabled() != null) this.journeyCompleteEnabled = req.journeyCompleteEnabled();

        if (req.friendRequestEnabled() != null) this.friendRequestEnabled = req.friendRequestEnabled();
        if (req.likeEnabled() != null) this.likeEnabled = req.likeEnabled();
        if (req.commentEnabled() != null) this.commentEnabled = req.commentEnabled();
        if (req.noticeEnabled() != null) this.noticeEnabled = req.noticeEnabled();
        if (req.marketingEnabled() != null) this.marketingEnabled = req.marketingEnabled();

        this.updatedAt = LocalDateTime.now();
    }

    public boolean canSend(NotificationSettingType type) {
        if (!this.allEnabled) return false;

        return switch (type) {
            case FRIEND_REQUEST -> this.friendRequestEnabled;
            case LIKE -> this.likeEnabled;
            case COMMENT -> this.commentEnabled;
            case NOTICE -> this.noticeEnabled;
            case MARKETING -> this.marketingEnabled;

            case DAY_END_JOURNEY_REMINDER -> this.dayEndJourneyReminderEnabled;
            case UNFINISHED_GOAL_REMINDER -> this.unfinishedGoalReminderEnabled;
            case JOURNEY_COMPLETE -> this.journeyCompleteEnabled;

            case ROUTINE -> this.routineEnabled;
        };
    }

}

