package com.loopon.term.infrastructure;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepository {
    private final TermJpaRepository termJpaRepository;

    @Override
    public List<Term> findAllForSignUp() {
        return termJpaRepository.findAllForSignUp();
    }

    @Override
    public Optional<Term> findById(Long termId) {
        return termJpaRepository.findById(termId);
    }
}
