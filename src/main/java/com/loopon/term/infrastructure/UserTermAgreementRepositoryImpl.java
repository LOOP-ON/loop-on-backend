package com.loopon.term.infrastructure;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserTermAgreementRepositoryImpl implements UserTermAgreementRepository {
    private final UserTermAgreementJpaRepository userTermAgreementJpaRepository;

    @Override
    public void save(UserTermAgreement userTermAgreement) {
        userTermAgreementJpaRepository.save(userTermAgreement);
    }

    @Override
    public void saveAll(List<UserTermAgreement> userTermAgreements) {
        userTermAgreementJpaRepository.saveAll(userTermAgreements);
    }

    @Override
    public Optional<UserTermAgreement> findByUserAndTerm(User user, Term term) {
        return userTermAgreementJpaRepository.findByUserAndTerm(user, term);
    }
}
