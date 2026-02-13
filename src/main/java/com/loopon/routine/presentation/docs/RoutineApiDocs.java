package com.loopon.routine.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @PostMapping
    ResponseEntity<CommonResponse<RoutineResponse.PostRoutinesDto>> postRoutine(
            @Valid @RequestBody RoutineRequest.CreateJourneyWithRoutineDto routineRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루틴 인증 (이미지 + 선택적 텍스트)", description = "루틴 인증 API입니다. **이미지 파일**은 필수이며, **추가 데이터(JSON)**는 선택사항입니다.")
    @ApiResponse(responseCode = "200", description = "루틴 인증에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @PostMapping(value = "/{progressId}/certify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<RoutineResponse.RoutineCertifyDto>> certifyRoutine(
            @Parameter(description = "진행 ID", required = true)
            @PathVariable Long progressId,

            @Parameter(description = "인증 이미지 파일 (.jpg, .png 등)", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("image") MultipartFile image,

            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    // 여정 미루기 사유 확인 API
    @Operation(summary = "루틴을 미룬 이유를 확인합니다.", description = "루틴 미루기 사유 확인 API")
    @ApiResponse(responseCode = "200", description = "루틴 미루기 사유 조회에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @GetMapping("/{progressId}/postpone-reason")
    ResponseEntity<CommonResponse<RoutineResponse.RoutinePostponeReasonDto>> postponeReason(
            @Parameter(description = "진행 ID", required = true) @PathVariable Long progressId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    // 여정 미루기 사유 수정 API
    @Operation(summary = "루틴 미룬 사유 수정", description = "루틴 미루기 사유 수정 API")
    @ApiResponse(responseCode = "200", description = "루틴 미루기 사유 수정에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @PatchMapping("/{progressId}/postpone-reason")
    ResponseEntity<CommonResponse<RoutineResponse.RoutinePostponeReasonEditDto>> editPostponeReason(
            @Parameter(description = "진행 ID", required = true) @PathVariable Long progressId,
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody RoutineRequest.editReasonDto body
    );
}
