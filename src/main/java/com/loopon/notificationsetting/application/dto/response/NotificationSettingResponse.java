package com.loopon.notificationsetting.application.dto.response;

import com.loopon.notificationsetting.domain.AlertMode;

import java.time.LocalTime;

public record NotificationSettingResponse(
        boolean allEnabled,

        // 루틴
        boolean routineEnabled,
        AlertMode routineAlertMode,

        // 미완료 목표 리마인드
        boolean unfinishedGoalReminderEnabled,

        // 하루 종료 - 오늘 여정 기록 리마인드
        boolean dayEndJourneyReminderEnabled,
        LocalTime dayEndJourneyReminderTime,

        // 여정 완료
        boolean journeyCompleteEnabled,

        // 시스템
        boolean friendRequestEnabled,
        boolean likeEnabled,
        boolean commentEnabled,
        boolean noticeEnabled,
        boolean marketingEnabled
) {
}
