package com.loopon.user.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.term.domain.Term;
import com.loopon.term.domain.UserTermAgreement;
import com.loopon.term.domain.repository.TermRepository;
import com.loopon.term.domain.repository.UserTermAgreementRepository;
import com.loopon.user.application.dto.command.UpdateProfileCommand;
import com.loopon.user.application.dto.command.UserSignUpCommand;
import com.loopon.user.application.dto.request.ChangePasswordRequest;
import com.loopon.user.application.dto.response.UserProfileResponse;
import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final UserTermAgreementRepository agreementRepository;

    private final PasswordEncoder passwordEncoder;

    public Long signUp(@Valid UserSignUpCommand command) {
        List<Term> allTerms = termRepository.findAllForSignUp();
        validateMandatoryTerms(allTerms, command.agreedTermIds());

        checkPasswordConfirmation(command.password(), command.confirmPassword());
        checkDuplicate(command.email(), command.nickname());

        User user = command.toEntity(passwordEncoder);
        Long userId = userRepository.save(user);

        saveUserTermAgreements(user, allTerms, command.agreedTermIds());

        return userId;
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(command.nickname(), command.bio(), command.statusMessage(), command.profileImageUrl(), command.visibility());

        return UserProfileResponse.of(user, null);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getProvider().equals(UserProvider.LOCAL)) {
            throw new BusinessException(ErrorCode.SOCIAL_USER_CANNOT_CHANGE_PASSWORD);
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.CURRENT_PASSWORD_MISMATCH);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    private void checkPasswordConfirmation(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private void checkDuplicate(String email, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void validateMandatoryTerms(List<Term> allTerms, List<Long> agreedTermIds) {
        List<Long> mandatoryTermIds = allTerms.stream()
                .filter(Term::getMandatory)
                .map(Term::getId)
                .toList();

        boolean isAllAgreed = agreedTermIds.containsAll(mandatoryTermIds);

        if (!isAllAgreed) {
            throw new BusinessException(ErrorCode.MANDATORY_TERM_NOT_AGREED);
        }
    }

    private void saveUserTermAgreements(User user, List<Term> allTerms, List<Long> agreedTermIds) {
        List<UserTermAgreement> agreements = allTerms.stream()
                .filter(term -> agreedTermIds.contains(term.getId()))
                .map(term -> UserTermAgreement.builder()
                        .user(user)
                        .term(term)
                        .build())
                .toList();

        agreementRepository.saveAll(agreements);
    }
}
