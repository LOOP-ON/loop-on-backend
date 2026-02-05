package com.loopon.goal.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoopGenerationRequest {

    @NotBlank(message = "목표는 필수입니다")
    private String goal;

    private int loopCount = 5;
}
