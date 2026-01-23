package com.loopon.user.application;

import com.loopon.user.application.dto.response.UserDuplicateCheckResponse;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;

    public UserDuplicateCheckResponse isEmailAvailable(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email);
        return UserDuplicateCheckResponse.of(isAvailable);
    }

    public UserDuplicateCheckResponse isNicknameAvailable(String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        return UserDuplicateCheckResponse.of(isAvailable);
    }
}
