package com.loopon.term.domain.repository;

import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserTermAgreementRepository {

    void save(UserTermAgreement userTermAgreement);

    void saveAll(List<UserTermAgreement> userTermAgreements);

    Optional<UserTermAgreement> findByUserAndTerm(User user, Term term);
}
