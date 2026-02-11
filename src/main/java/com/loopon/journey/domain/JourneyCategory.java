package com.loopon.journey.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JourneyCategory {
    ROUTINE("생활 루틴"),
    GROWTH("역량 강화"),
    MENTAL("내면 관리");

    private final String description;
}
