package com.loopon.journey.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "09. 여정(Journey)", description = "여정 생성 및 조회 API")
public interface JourneyApiDocs {

    @Operation(summary = "여정을 시작합니다.", description = "새로운 여정 생성 API")
    @ApiResponse(responseCode = "200", description = "여정생성에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.PostJourneyGoalDto>> postJourneyGoal(
            @Valid @RequestBody JourneyRequest.AddJourneyDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루틴을 미룹니다.", description = "진행 중인 여정의 루틴을 미루는 API")
    @ApiResponse(responseCode = "200", description = "루틴 미루기에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeRoutine(
            @PathVariable Long journeyId,
            @PathVariable Long routineId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    //여정에 해당하는 루틴 전체 미루기 API
    @Operation(summary = "미완료 된 루틴을 전체 미룹니다.", description = "진행 중인 여정의 루틴을 미루는 API")
    @ApiResponse(responseCode = "200", description = "루틴 미루기에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeAllRoutine(
            @PathVariable Long journeyId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "현재 진행 중인 여정 조회", description = "사용자가 현재 진행 중인 여정과 오늘의 루틴 진행 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "현재 진행 중인 여정 조회에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.CurrentJourneyDto>> getCurrentJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루프 재생성", description = "마음에 들지 않는 루프를 새로운 내용으로 재생성합니다")
    @ApiResponse(responseCode = "200", description = "루프 재생성에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<LoopRegenerationResponse>> regenerateLoop(
            @Valid @RequestBody LoopRegenerationRequest request
    );
}
