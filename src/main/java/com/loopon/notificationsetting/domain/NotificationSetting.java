package com.loopon.notificationsetting.domain;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "notification_settings", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
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

    public void update(UpdateRequest request) {
        if (request.allEnabled != null) this.allEnabled = request.allEnabled;
        if (request.routineEnabled != null) this.routineEnabled = request.routineEnabled;
        if (request.routineAlertMode != null) this.routineAlertMode = request.routineAlertMode;
        if (request.unfinishedGoalReminderEnabled != null)
            this.unfinishedGoalReminderEnabled = request.unfinishedGoalReminderEnabled;
        if (request.dayEndJourneyReminderEnabled != null)
            this.dayEndJourneyReminderEnabled = request.dayEndJourneyReminderEnabled;
        if (request.dayEndJourneyReminderTime != null)
            this.dayEndJourneyReminderTime = request.dayEndJourneyReminderTime;
        if (request.journeyCompleteEnabled != null)
            this.journeyCompleteEnabled = request.journeyCompleteEnabled;
        if (request.friendRequestEnabled != null)
            this.friendRequestEnabled = request.friendRequestEnabled;
        if (request.likeEnabled != null) this.likeEnabled = request.likeEnabled;
        if (request.commentEnabled != null) this.commentEnabled = request.commentEnabled;
        if (request.noticeEnabled != null) this.noticeEnabled = request.noticeEnabled;
        if (request.marketingEnabled != null) this.marketingEnabled = request.marketingEnabled;

        this.updatedAt = LocalDateTime.now();
    }

    @Getter
    @Builder
    public static class UpdateRequest {
        private Boolean allEnabled;
        private Boolean routineEnabled;
        private AlertMode routineAlertMode;
        private Boolean unfinishedGoalReminderEnabled;
        private LocalTime unfinishedGoalReminderTime;
        private Boolean dayEndJourneyReminderEnabled;
        private LocalTime dayEndJourneyReminderTime;
        private Boolean journeyCompleteEnabled;
        private Boolean friendRequestEnabled;
        private Boolean likeEnabled;
        private Boolean commentEnabled;
        private Boolean noticeEnabled;
        private Boolean marketingEnabled;
    }

    //실제 알림 전송 과정에서 알림 권한 확인용
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

    //리마인드 알림 기본 시각 23시
    @PrePersist
    public void prePersist() {
        if (dayEndJourneyReminderTime == null) {
            this.dayEndJourneyReminderTime = LocalTime.of(23, 0);
        }
    }
}
