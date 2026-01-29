package com.loopon.term.application.dto.response;

import com.loopon.term.domain.Term;

public record TermDetailResponse(
        Long termId,
        String code,
        String title,
        String content,
        boolean mandatory,
        String version
) {

    public static TermDetailResponse from(Term term) {
        return new TermDetailResponse(
                term.getId(),
                term.getCode().name(),
                term.getTitle(),
                term.getContent(),
                term.getMandatory(),
                term.getVersion()
        );
    }
}
