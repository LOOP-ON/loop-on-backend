package com.loopon.setting.application.dto.request;

import com.loopon.setting.domain.AlertMode;
import com.loopon.setting.domain.NotificationSetting;

import java.time.LocalTime;

public record NotificationSettingPatchRequest(
        Boolean allEnabled,

        Boolean routineEnabled,
        AlertMode routineAlertMode,

        Boolean unfinishedGoalReminderEnabled,

        Boolean dayEndJourneyReminderEnabled,
        LocalTime dayEndJourneyReminderTime,

        Boolean journeyCompleteEnabled,

        Boolean friendRequestEnabled,
        Boolean likeEnabled,
        Boolean commentEnabled,
        Boolean noticeEnabled,
        Boolean marketingEnabled
) {
    public NotificationSetting.UpdateRequest toUpdateRequest() {
        return NotificationSetting.UpdateRequest.builder()
                .allEnabled(this.allEnabled)
                .routineEnabled(this.routineEnabled)
                .routineAlertMode(this.routineAlertMode)
                .unfinishedGoalReminderEnabled(this.unfinishedGoalReminderEnabled)
                .dayEndJourneyReminderEnabled(this.dayEndJourneyReminderEnabled)
                .dayEndJourneyReminderTime(this.dayEndJourneyReminderTime)
                .journeyCompleteEnabled(this.journeyCompleteEnabled)
                .friendRequestEnabled(this.friendRequestEnabled)
                .likeEnabled(this.likeEnabled)
                .commentEnabled(this.commentEnabled)
                .noticeEnabled(this.noticeEnabled)
                .marketingEnabled(this.marketingEnabled)
                .build();
    }
}