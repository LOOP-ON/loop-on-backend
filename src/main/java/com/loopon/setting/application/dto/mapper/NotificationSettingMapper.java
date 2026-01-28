package com.loopon.setting.application.dto.mapper;

import com.loopon.setting.application.dto.response.NotificationSettingResponse;
import com.loopon.setting.domain.NotificationSetting;

public class NotificationSettingMapper {
    public static NotificationSettingResponse toResponse(NotificationSetting s) {
        return new NotificationSettingResponse(
                s.isAllEnabled(),
                s.isRoutineEnabled(),
                s.getRoutineAlertMode(),
                s.isUnfinishedGoalReminderEnabled(),
                s.getUnfinishedGoalReminderTime(),
                s.isDayEndJourneyReminderEnabled(),
                s.getDayEndJourneyReminderTime(),
                s.isJourneyCompleteEnabled(),
                s.isFriendRequestEnabled(),
                s.isLikeEnabled(),
                s.isCommentEnabled(),
                s.isNoticeEnabled(),
                s.isMarketingEnabled()
        );
    }
}