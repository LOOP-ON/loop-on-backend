package com.loopon.term.application.dto.response;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.TermsCode;

public record TermResponse(
        Long termId,
        TermsCode code,
        String title,
        boolean mandatory
) {

    public static TermResponse from(Term term) {
        return new TermResponse(
                term.getId(),
                term.getCode(),
                term.getTitle(),
                term.getMandatory()
        );
    }
}
