package com.loopon.user.application.dto.command;

import com.loopon.user.domain.User;
import com.loopon.user.domain.UserProvider;
import com.loopon.user.domain.UserRole;
import com.loopon.user.domain.UserStatus;
import com.loopon.user.domain.policy.UserValidationPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

public record UserSignUpCommand(
        String email,

        @NotBlank
        @Pattern(regexp = UserValidationPolicy.PASSWORD_REGEX, message = UserValidationPolicy.PASSWORD_MESSAGE)
        String password,

        @NotBlank
        String confirmPassword,
        String name,
        String nickname,
        List<Long> agreedTermIds
) {

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .provider(UserProvider.LOCAL)
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .profileImageUrl(null)
                .userStatus(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Override
    public String toString() {
        return "UserSignUpCommand[" +
                "email=" + email +
                ", name=" + name +
                ", nickname=" + nickname +
                ", agreedTermIds=" + agreedTermIds +
                ", password=****" +
                ", confirmPassword=****" +
                "]";
    }
}
