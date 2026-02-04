package com.loopon.journey.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import com.loopon.journey.application.service.LoopRegenerationService;
import com.loopon.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
@Tag(name = "Journeys", description = "루프 재생성 API")
public class JourneyController {
    
    private final LoopRegenerationService loopRegenerationService;
    
    @PutMapping("/{journeyId}/regenerate")
    @Operation(summary = "루프 재생성", description = "마음에 들지 않는 루프를 새로운 내용으로 재생성합니다")
    public CommonResponse<LoopRegenerationResponse> regenerateLoop(
            @PathVariable Long journeyId,
            @Valid @RequestBody LoopRegenerationRequest request,
            @AuthenticationPrincipal User user) {
        
        LoopRegenerationResponse response = loopRegenerationService.regenerateLoop(journeyId, request, user);
        
        return CommonResponse.onSuccess(response);
    }
}