package com.loopon.term.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.application.dto.response.TermDetailResponse;
import com.loopon.term.application.dto.response.TermResponse;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermQueryService {
    private final TermRepository termRepository;

    public List<TermResponse> getTermsForSignUp() {
        return termRepository.findAllForSignUp().stream()
                .map(TermResponse::from)
                .toList();
    }

    public TermDetailResponse getTermDetail(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

        return TermDetailResponse.from(term);
    }
}
