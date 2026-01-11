package com.loopon.user.application;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import com.loopon.user.application.dto.command.UserSignUpCommand;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public Long signUp(UserSignUpCommand command) {
        checkPasswordConfirmation(command.password(), command.confirmPassword());
        checkDuplicate(command.email(), command.nickname());

        User user = command.toEntity(passwordEncoder);
        Long userId = userRepository.save(user);

        log.info("UserCommandService.signUp - 회원 가입 완료(email: {}, nickname: {})", user.getEmail(), user.getNickname());

        return userId;
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
}
