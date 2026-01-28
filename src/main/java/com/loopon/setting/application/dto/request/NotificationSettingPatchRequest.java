package com.loopon.setting.application.dto.request;

import com.loopon.setting.domain.AlertMode;
import java.time.LocalTime;

public record NotificationSettingPatchRequest(
        Boolean allEnabled,

        Boolean routineEnabled,
        AlertMode routineAlertMode,

        Boolean unfinishedGoalReminderEnabled,
        LocalTime unfinishedGoalReminderTime,

        Boolean dayEndJourneyReminderEnabled,
        LocalTime dayEndJourneyReminderTime,

        Boolean journeyCompleteEnabled,

        Boolean friendRequestEnabled,
        Boolean likeEnabled,
        Boolean commentEnabled,
        Boolean noticeEnabled,
        Boolean marketingEnabled
) {}