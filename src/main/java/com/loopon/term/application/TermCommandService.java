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

    public void updateTermAgreement(String email, Long termId, boolean agree) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

        if (term.getMandatory()) {
            throw new BusinessException(ErrorCode.MANDATORY_TERM_CANNOT_BE_REVOKED);
        }

        User user = userRepository.findByEmail(email);

        UserTermAgreement agreement = agreementRepository.findByUserAndTerm(user, term)
                .orElse(null);

        if (agree) {
            if (agreement == null) {
                agreementRepository.save(UserTermAgreement.builder()
                        .user(user)
                        .term(term)
                        .build()
                );

            } else {
                agreement.restore();
            }
        } else {
            if (agreement != null) {
                agreement.revoke();
            }
        }
    }
}
