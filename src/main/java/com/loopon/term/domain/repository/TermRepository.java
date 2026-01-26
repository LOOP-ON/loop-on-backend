package com.loopon.term.domain.repository;

import com.loopon.term.domain.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository {

    List<Term> findAllForSignUp();

    Optional<Term> findById(Long termId);
}
