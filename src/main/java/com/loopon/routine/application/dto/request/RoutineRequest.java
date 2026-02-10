package com.loopon.routine.application.dto.request;

import java.util.List;

public class RoutineRequest {

    public record AddJRoutineDto(
            Long journeyId,
            List<RoutineDataDto> routines
    ) {}

    public record RoutineDataDto(
            String content,
            String notificationTime // "15:00"
    ) {}

    public record RoutineCertifyDataDto(){ }
}
