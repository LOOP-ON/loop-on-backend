package com.loopon.setting.domain;

public enum NotificationSettingType {

    // 시스템/소셜
    FRIEND_REQUEST,
    LIKE,
    COMMENT,
    NOTICE,
    MARKETING,

    // 루틴
    ROUTINE,

    // 여정/목표
    DAY_END_JOURNEY_REMINDER,     // 오늘 여정 기록
    UNFINISHED_GOAL_REMINDER,     // 미완료 목표 리마인드
    JOURNEY_COMPLETE              // 여정 완료 알림
}