package com.loopon.term.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.TermRepository;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.domain.User;
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
        User user = getUser(email);
        Term term = getTerm(termId);

        if (isAgree) {
            processAgreement(user, term);
        } else {
            processRevocation(user, term);
        }
    }

    private void processAgreement(User user, Term term) {
        agreementRepository.findByUserAndTerm(user, term)
                .ifPresentOrElse(
                        UserTermAgreement::restore,
                        () -> agreementRepository.save(
                                UserTermAgreement.builder()
                                        .user(user)
                                        .term(term)
                                        .build())
                );
    }

    private void processRevocation(User user, Term term) {
        validateRevocation(term);

        agreementRepository.findByUserAndTerm(user, term)
                .ifPresent(UserTermAgreement::revoke);
    }

    private void validateRevocation(Term term) {
        if (term.getMandatory()) {
            throw new BusinessException(ErrorCode.MANDATORY_TERM_CANNOT_BE_REVOKED);
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Term getTerm(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));
    }
}
