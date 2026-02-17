package com.loopon.term.infrastructure;

import com.loopon.term.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TermJpaRepository extends JpaRepository<Term, Long> {

    @Query("""
            SELECT t FROM Term t
                ORDER BY t.mandatory DESC, t.id ASC
            """)
    List<Term> findAllForSignUp();
}
