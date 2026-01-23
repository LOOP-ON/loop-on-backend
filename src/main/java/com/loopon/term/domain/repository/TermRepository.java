package com.loopon.term.domain.repository;

import com.loopon.term.domain.Term;

import java.util.List;

public interface TermRepository {

    List<Term> findAllForSignUp();
}
