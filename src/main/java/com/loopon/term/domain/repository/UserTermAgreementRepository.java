package com.loopon.term.domain.repository;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.user.infrastructure.persistence.jpa.UserJpaEntity;

import java.util.List;
import java.util.Optional;

public interface UserTermAgreementRepository {

    void save(UserTermAgreement userTermAgreement);

    void saveAll(List<UserTermAgreement> userTermAgreements);

    Optional<UserTermAgreement> findByUserAndTerm(UserJpaEntity userJpaEntity, Term term);
}
