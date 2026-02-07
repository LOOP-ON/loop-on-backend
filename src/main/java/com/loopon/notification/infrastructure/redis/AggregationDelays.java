package com.loopon.notification.infrastructure.redis;

//집계 시간 설정
public final class AggregationDelays {
    public static final long CHALLENGE_LIKE_MS = 5 * 60 * 1000L; // 5분
    private AggregationDelays() {}
}