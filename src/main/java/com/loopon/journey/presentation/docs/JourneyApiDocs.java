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
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    //여정에 해당하는 루틴 전체 미루기 API
    @Operation(summary = "미완료 된 루틴을 미룹니다.", description = "진행 중인 여정의 루틴을 미루는 API")
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

    @Operation(summary = "여권 여정 목록 조회", description = "여권에서 보이는 여정들을 목록 조회합니다.")
    @ApiResponse(responseCode = "200", description = "여정 목록 조회에 성공했습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> getJourneyList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "여권 여정 검색하기", description = "키워드와 카테고리 분류를 통해 여정을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "여정 검색에 성공했습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    ResponseEntity<CommonResponse<Slice<JourneyResponse.JourneyPreviewDto>>> searchJourney(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam String keyword,
            @RequestParam @Size(min = 3, max = 3) List<Boolean> categories,
            @PageableDefault Pageable pageable
    );
}
