package com.loopon.expedition.application.dto.command;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
public record ExpeditionSearchCommand(
        String keyword,
        List<Boolean> categories,
        Pageable pageable,
        Long userId
) {
}
