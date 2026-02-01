package com.loopon.journey.presentation;

import com.loopon.journey.application.dto.request.JourneyGenerationRequest;
import com.loopon.journey.application.dto.request.JourneyRegenerationRequest;
import com.loopon.journey.application.dto.response.JourneyGenerationResponse;
import com.loopon.journey.application.service.JourneyGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/journeys")
@Tag(name = "Journey Generation", description = "여정 생성 API")
public class JourneyController {

    private final JourneyGenerationService journeyGenerationService;

    public JourneyController(JourneyGenerationService journeyGenerationService) {
        this.journeyGenerationService = journeyGenerationService;
    }

    @PostMapping("/generate")
    @Operation(summary = "여정 생성", description = "목표를 기반으로 5개의 여정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여정 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<JourneyGenerationResponse> generateJourneys(
            @Valid @RequestBody JourneyGenerationRequest request) {

        JourneyGenerationResponse response = journeyGenerationService.generateJourneys(request.goal());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/regenerate")
    @Operation(summary = "여정 재생성", description = "제외할 여정들을 제외하고 새로운 여정들을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여정 재생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<JourneyGenerationResponse> regenerateJourneys(
            @Valid @RequestBody JourneyRegenerationRequest request) {

        JourneyGenerationResponse response = journeyGenerationService.regenerateJourneys(
                request.goal(),
                request.excludeJourneyTitles()
        );
        return ResponseEntity.ok(response);
    }
}