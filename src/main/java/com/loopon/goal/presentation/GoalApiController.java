package com.loopon.goal.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.goal.application.dto.request.LoopGenerationRequest;
import com.loopon.goal.application.dto.response.LoopGenerationResponse;
import com.loopon.goal.application.service.LoopGenerationService;
import com.loopon.goal.presentation.docs.GoalApiDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalApiController implements GoalApiDocs {
    private final LoopGenerationService loopGenerationService;

    @Override
    @PostMapping("/loops")
    public ResponseEntity<CommonResponse<LoopGenerationResponse>> generateLoops(
            @Valid @RequestBody LoopGenerationRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        LoopGenerationResponse response = loopGenerationService.generateLoops(request, principalDetails.getUserId());

        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
