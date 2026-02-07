package com.loopon.goal.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.goal.application.dto.request.LoopGenerationRequest;
import com.loopon.goal.application.dto.response.LoopGenerationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "12. 목표(Goal)", description = "목표 및 루프 생성 API")
public interface GoalApiDocs {

    @Operation(summary = "목표 기반 루프 생성", description = "사용자가 입력한 목표를 바탕으로 5개의 연관 루프를 생성합니다")
    @ApiResponse(responseCode = "200", description = "루프 생성에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<LoopGenerationResponse>> generateLoops(
            @Valid @RequestBody LoopGenerationRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
