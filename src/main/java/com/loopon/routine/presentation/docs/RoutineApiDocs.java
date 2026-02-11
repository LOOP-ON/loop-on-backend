package com.loopon.routine.presentation.docs;

import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "10. 루틴(Routine)", description = "루틴 생성 및 조회 API")
public interface RoutineApiDocs {

    @Operation(summary = "여정 시작 및 루틴 생성 (온보딩 완료)",
            description = "온보딩의 마지막 단계입니다. 1~3단계에서 수집한 [목표, 카테고리, 선택한 루프, 루틴 3개]를 모두 받아 **새로운 여정을 생성**하고, 오늘부터 3일간의 실천표를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "여정 및 루틴 생성 성공 (여정 ID 반환)", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<RoutineResponse.PostRoutinesDto>> postRoutine(
            @Valid @RequestBody RoutineRequest.CreateJourneyWithRoutineDto routineRequest,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루틴을 인증합니다.", description = "루틴 인증, 이미지 업로드 API")
    @ApiResponse(responseCode = "200", description = "루틴 인증에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @PostMapping(value = "/{progressId}/certify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<RoutineResponse.RoutineCertifyDto>> certifyRoutine(
            @PathVariable Long progressId,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );
}
