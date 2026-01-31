package com.loopon.Routine.application.dto.request;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class RoutineRequest {

    public record AddJRoutineDto(
            Long journeyId,
            List<RoutineDataDto> routines
    ) {}

    public record RoutineDataDto(
            String content,
            String notificationTime // "15:00"
    ) {}
}
