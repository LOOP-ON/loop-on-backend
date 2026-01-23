package com.loopon.term.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.term.application.TermQueryService;
import com.loopon.term.application.dto.response.TermResponse;
import com.loopon.term.presentation.docs.TermApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermApiController implements TermApiDocs {
    private final TermQueryService termQueryService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<TermResponse>>> getTermsForSignUp() {
        List<TermResponse> responses = termQueryService.getTermsForSignUp();
        return ResponseEntity.ok(CommonResponse.onSuccess(responses));
    }
}
