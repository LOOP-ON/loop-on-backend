package com.loopon.user.application.dto.command;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public record UserSignUpCommand(
        String email,
        String password,
        String confirmPassword,
        String name,
        String nickname,
        LocalDate birthDate
) {

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .provider(UserProvider.LOCAL)
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .birthDate(birthDate)
                .profileImageUrl(null)
                .userStatus(UserStatus.ACTIVE)
                .build();
    }
}
