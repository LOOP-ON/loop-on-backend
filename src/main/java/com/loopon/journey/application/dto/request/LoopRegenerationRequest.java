package com.loopon.journey.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoopRegenerationRequest {
    
    @NotBlank(message = "기존 루프 목표는 필수입니다")
    private String originalGoal;
    
    @NotBlank(message = "전체 목표는 필수입니다")
    private String mainGoal;
}
