package com.loopon.journey.presentation.docs;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.journey.application.dto.request.JourneyRequest;
import com.loopon.journey.application.dto.request.LoopRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyContinueResponse;
import com.loopon.journey.application.dto.response.JourneyResponse;
import com.loopon.journey.application.dto.response.LoopRegenerationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "09. 여정(Journey)", description = "여정 관리 및 AI 분석 API")
public interface JourneyApiDocs {

    @Operation(summary = "다음 여정 순서 조회 (온보딩 진입 시)", description = "온보딩 페이지 진입 시, 이번에 생성될 여정이 몇 번째인지 조회합니다. (예: '첫 번째 여정의 루틴을...')")
    @ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.JourneyOrderDto>> getNextJourneyOrder(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "목표 분석 및 AI 행동 추천", description = "사용자의 목표와 카테고리를 기반으로 AI가 실천 가능한 루프(행동) 5가지를 추천합니다. (DB 저장 X, 단순 분석)")
    @ApiResponse(responseCode = "200", description = "AI 행동 추천 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.GoalRecommendationResponse>> analyzeGoal(
            @Valid @RequestBody JourneyRequest.GoalRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루프 재생성 (AI)", description = "AI가 추천한 루프가 마음에 들지 않을 경우, 새로운 내용으로 1개를 다시 생성합니다.")
    @ApiResponse(responseCode = "200", description = "루프 재생성 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<LoopRegenerationResponse>> regenerateLoop(
            @Valid @RequestBody LoopRegenerationRequest request
    );

    @Operation(summary = "여정 계속하기", description = "중단되거나 보류된 여정을 다시 이어서 진행합니다.")
    @ApiResponse(responseCode = "200", description = "여정 계속하기 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyContinueResponse>> continueJourney(
            @Parameter(description = "여정 ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long journeyId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "루틴 미루기", description = "진행 중인 여정의 특정 루틴들을 미룹니다.")
    @ApiResponse(responseCode = "200", description = "루틴 미루기 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.PostponeRoutineDto>> postponeAllRoutine(
            @Parameter(description = "여정 ID", required = true, in = ParameterIn.PATH)
            @PathVariable Long journeyId,
            @Valid @RequestBody JourneyRequest.PostponeRoutineDto reqBody,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "현재 진행 중인 여정 조회", description = "사용자가 현재 진행 중인 여정과 오늘의 루틴 진행 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.CurrentJourneyDto>> getCurrentJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    //여정 완료 후 기록하기
    @Operation(summary = "여정 완료 후 기록 API", description = "사용자가 3일 루프를 끝낸 뒤 여정 완료 -> 리포트 생성API")
    @ApiResponse(responseCode = "200", description = "현재 진행 중인 여정 조회에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.JourneyRecordDto>> postJourneyRecord(
            @PathVariable Long journeyId,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    //사용자 달별로 루틴 진행 개수 확인
    @Operation(summary = "monthly 진행 루틴 확인", description = "달별 루틴 진행 개수 확인 API")
    @ApiResponse(responseCode = "200", description = "현재 진행 중인 여정 조회에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<List<JourneyResponse.MonthlyCompletedDto>>> getMonthlyCompleted(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "해당 날짜 리포트 불러오기", description = "사용자가 해당 날짜에 진행한 리포트를 생성 후 조회힙니다.")
    @ApiResponse(responseCode = "200", description = "현재 진행 중인 여정 조회에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<JourneyResponse.DailyJourneyReportDto>> getDailyReport(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(summary = "여권 여정 목록 조회", description = "여권(마이페이지)에서 지난 여정들의 목록을 조회합니다. (무한 스크롤)")
    @ApiResponse(responseCode = "200", description = "목록 조회 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> getJourneyList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "여권 여정 검색하기", description = "키워드와 카테고리 필터를 통해 지난 여정을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> searchJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails,

            @ParameterObject
            @Valid @ModelAttribute JourneyRequest.JourneySearchCondition searchCondition,

            @PageableDefault Pageable pageable
    );
}
