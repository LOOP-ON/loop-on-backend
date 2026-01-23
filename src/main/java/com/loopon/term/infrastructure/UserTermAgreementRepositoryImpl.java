package com.loopon.term.infrastructure;

import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserTermAgreementRepositoryImpl implements UserTermAgreementRepository {
    private final UserTermAgreementJpaRepository userTermAgreementJpaRepository;

    @Override
    public void saveAll(List<UserTermAgreement> userTermAgreements) {
        userTermAgreementJpaRepository.saveAll(userTermAgreements);
    }
}
