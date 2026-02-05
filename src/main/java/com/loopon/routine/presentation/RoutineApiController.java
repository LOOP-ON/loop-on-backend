package com.loopon.routine.presentation;

import com.loopon.routine.application.dto.request.RoutineRequest;
import com.loopon.routine.application.dto.response.RoutineResponse;
import com.loopon.routine.domain.service.RoutineCommandService;
import com.loopon.routine.presentation.docs.RoutineApiDocs;
import com.loopon.global.domain.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineApiController implements RoutineApiDocs {
    private final RoutineCommandService routineCommandService;

    @PostMapping("/")
    @Override
    public ResponseEntity<CommonResponse<RoutineResponse.PostRoutinesDto>> postRoutine(
            @Valid @RequestBody RoutineRequest.AddJRoutineDto routineRequest
    ){
        RoutineResponse.PostRoutinesDto PostRoutines = routineCommandService.postRoutine(routineRequest);

        return ResponseEntity.ok(CommonResponse.onSuccess(PostRoutines));
    }
}
