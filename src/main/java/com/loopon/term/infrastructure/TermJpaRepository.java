package com.loopon.term.infrastructure;

import com.loopon.term.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TermJpaRepository extends JpaRepository<Term, Long> {

    @Query("select t From Term t order by t.mandatory desc, t.id asc")
    List<Term> findAllForSignUp();
}
