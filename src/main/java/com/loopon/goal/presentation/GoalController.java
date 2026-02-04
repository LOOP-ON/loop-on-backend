package com.loopon.goal.presentation;

import com.loopon.goal.application.dto.request.LoopGenerationRequest;
import com.loopon.goal.application.dto.response.LoopGenerationResponse;
import com.loopon.goal.application.service.LoopGenerationService;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "목표 및 루프 생성 API")
public class GoalController {
    
    private final LoopGenerationService loopGenerationService;
    
    @PostMapping("/loops")
    @Operation(summary = "목표 기반 루프 생성", description = "사용자가 입력한 목표를 바탕으로 5개의 연관 루프를 생성합니다")
    public CommonResponse<LoopGenerationResponse> generateLoops(
            @Valid @RequestBody LoopGenerationRequest request,
            @AuthenticationPrincipal User user) {
        
        LoopGenerationResponse response = loopGenerationService.generateLoops(request, user);
        
        return CommonResponse.onSuccess(response);
    }
}