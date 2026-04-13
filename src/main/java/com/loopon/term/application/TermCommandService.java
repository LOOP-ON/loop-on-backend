package com.loopon.term.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.TermRepository;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.domain.UserJpaEntity;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TermCommandService {
    private final TermRepository termRepository;
    private final UserRepository userRepository;
    private final UserTermAgreementRepository agreementRepository;

    public void updateTermAgreement(String email, Long termId, boolean isAgree) {
        UserJpaEntity userJpaEntity = getUser(email);
        Term term = getTerm(termId);

        if (isAgree) {
            processAgreement(userJpaEntity, term);
        } else {
            processRevocation(userJpaEntity, term);
        }
    }

    private void processAgreement(UserJpaEntity userJpaEntity, Term term) {
        agreementRepository.findByUserAndTerm(userJpaEntity, term)
                .ifPresentOrElse(
                        UserTermAgreement::restore,
                        () -> agreementRepository.save(
                                UserTermAgreement.builder()
                                        .userJpaEntity(userJpaEntity)
                                        .term(term)
                                        .build())
                );
    }

    private void processRevocation(UserJpaEntity userJpaEntity, Term term) {
        validateRevocation(term);

        agreementRepository.findByUserAndTerm(userJpaEntity, term)
                .ifPresent(UserTermAgreement::revoke);
    }

    private void validateRevocation(Term term) {
        if (term.getMandatory()) {
            throw new BusinessException(ErrorCode.MANDATORY_TERM_CANNOT_BE_REVOKED);
        }
    }

    private UserJpaEntity getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Term getTerm(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));
    }
}
