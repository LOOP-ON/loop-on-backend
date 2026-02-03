package com.loopon.Routine.presentation;

import com.loopon.Routine.application.dto.request.RoutineRequest;
import com.loopon.Routine.application.dto.response.RoutineResponse;
import com.loopon.Routine.domain.service.RoutineCommandService;
import com.loopon.Routine.presentation.docs.RoutineApiDocs;
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
