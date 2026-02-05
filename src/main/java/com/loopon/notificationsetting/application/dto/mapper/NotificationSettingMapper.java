package com.loopon.notificationsetting.application.dto.mapper;

import com.loopon.notificationsetting.application.dto.response.NotificationSettingResponse;
import com.loopon.notificationsetting.domain.NotificationSetting;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationSettingMapper {
    public static NotificationSettingResponse toResponse(NotificationSetting s) {
        return new NotificationSettingResponse(
                s.isAllEnabled(),
                s.isRoutineEnabled(),
                s.getRoutineAlertMode(),
                s.isUnfinishedGoalReminderEnabled(),
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