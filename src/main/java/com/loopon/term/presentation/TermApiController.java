package com.loopon.term.presentation;

import com.loopon.global.domain.dto.CommonResponse;
import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.term.application.TermCommandService;
import com.loopon.term.application.TermQueryService;
import com.loopon.term.application.dto.response.TermDetailResponse;
import com.loopon.term.application.dto.response.TermResponse;
import com.loopon.term.presentation.docs.TermApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermApiController implements TermApiDocs {
    private final TermQueryService termQueryService;
    private final TermCommandService termCommandService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<TermResponse>>> getTermsForSignUp() {
        List<TermResponse> responses = termQueryService.getTermsForSignUp();
        return ResponseEntity.ok(CommonResponse.onSuccess(responses));
    }

    @Override
    @GetMapping("/{termId}")
    public ResponseEntity<CommonResponse<TermDetailResponse>> getTermDetail(@PathVariable Long termId) {
        TermDetailResponse response = termQueryService.getTermDetail(termId);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @PatchMapping("/{termId}")
    public ResponseEntity<CommonResponse<Void>> updateTermAgreement(
            @AuthenticationPrincipal PrincipalDetails principal,
            @PathVariable Long termId,
            @RequestParam boolean agree
    ) {
        termCommandService.updateTermAgreement(principal.getUsername(), termId, agree);
        return ResponseEntity.ok(CommonResponse.onSuccess());
    }
}
