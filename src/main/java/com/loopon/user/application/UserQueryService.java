package com.loopon.user.application;

import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserQueryService {
    private final UserRepository userRepository;

    public UserDuplicateCheckResponse isEmailAvailable(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email);
        log.info("UserQueryService.isEmailAvailable - 이메일 중복 확인(email: {}, isAvailable: {})", email, isAvailable);
        return UserDuplicateCheckResponse.of(isAvailable);
    }

    public UserDuplicateCheckResponse isNicknameAvailable(String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        log.info("UserQueryService.isNicknameAvailable - 닉네임 중복 확인(nickname: {}, isAvailable: {})", nickname, isAvailable);
        return UserDuplicateCheckResponse.of(isAvailable);
    }
}
