package com.loopon.term.application.dto.response;

import com.loopon.term.domain.Term;

public record TermResponse(
        Long termId,
        String code,
        String title,
        boolean mandatory
) {

    public static TermResponse from(Term term) {
        return new TermResponse(
                term.getId(),
                term.getCode().name(),
                term.getTitle(),
                term.getMandatory()
        );
    }
}
