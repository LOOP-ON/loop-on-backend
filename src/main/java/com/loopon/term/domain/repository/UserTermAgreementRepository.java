package com.loopon.term.domain.repository;

import com.loopon.term.domain.UserTermAgreement;

import java.util.List;

public interface UserTermAgreementRepository {

    void saveAll(List<UserTermAgreement> userTermAgreements);
}
