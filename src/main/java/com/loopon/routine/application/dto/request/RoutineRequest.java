package com.loopon.routine.application.dto.request;

import com.loopon.journey.domain.JourneyCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;

public class RoutineRequest {

    public record CreateJourneyWithRoutineDto(
            @Schema(description = "1단계 목표", example = "건강한 생활 만들기")
            @NotBlank
            String goal,

            @Schema(description = "1단계 카테고리", example = "ROUTINE")
            @NotNull
            JourneyCategory category,

            @Schema(description = "2단계에서 선택한 루프 내용 (여정의 핵심 테마)", example = "아침에 물 마시기")
            @NotBlank
            String selectedLoop,

            @Schema(description = "3단계 루틴 목록 (3개)")
            @Valid
            @Size(min = 3, max = 3, message = "루틴은 정확히 3개여야 합니다.")
            List<RoutineItemDto> routines
    ) {}

    public record RoutineItemDto(
            @Schema(description = "할 일 내용", example = "물 한 컵 따르기")
            @NotBlank
            String content,

            @Schema(description = "알림 시간", example = "08:00")
            @NotNull
            LocalTime time
    ) {}
}
