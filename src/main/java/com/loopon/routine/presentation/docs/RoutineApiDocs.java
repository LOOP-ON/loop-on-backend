package com.loopon.routine.presentation.docs;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "11. 루틴(Routine)", description = "루틴 생성 및 조회 API")
public interface RoutineApiDocs {

    @Operation(summary = "루틴을 생성합니다.", description = "새로운 루틴 생성 API")
    @ApiResponse(responseCode = "200", description = "루틴 생성에 성공하였습니다.", useReturnTypeSchema = true)
    @CommonBadRequestResponseDocs
    @CommonInternalServerErrorResponseDocs
    @PostMapping
    ResponseEntity<CommonResponse<RoutineResponse.PostRoutinesDto>> postRoutine(
            @Valid @RequestBody RoutineRequest.AddJRoutineDto routineRequest
    );
}
