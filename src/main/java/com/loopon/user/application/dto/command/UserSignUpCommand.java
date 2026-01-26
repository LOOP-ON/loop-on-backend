package com.loopon.user.application.dto.command;

import com.loopon.user.domain.User;
import com.loopon.user.domain.policy.UserValidationPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public record UserSignUpCommand(
        String email,

        @NotBlank
        @Pattern(regexp = UserValidationPolicy.PASSWORD_REGEX, message = UserValidationPolicy.PASSWORD_MESSAGE)
        String password,

        @NotBlank
        String confirmPassword,
        String nickname,
        String profileImageUrl,
        List<Long> agreedTermIds
) {
    public static UserSignUpCommand of(
            String email,
            String password,
            String confirmPassword,
            String nickname,
            String profileImageUrl,
            List<Long> agreedTermIds
    ) {

        return new UserSignUpCommand(
                email,
                password,
                confirmPassword,
                nickname,
                profileImageUrl,
                agreedTermIds
        );
    }

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.createLocalUser(
                email,
                nickname,
                passwordEncoder.encode(password),
                profileImageUrl
        );
    }

    @Override
    public String toString() {
        return "UserSignUpCommand[" +
                "email=" + email +
                ", nickname=" + nickname +
                ", agreedTermIds=" + agreedTermIds +
                ", password=****" +
                ", confirmPassword=****" +
                "]";
    }
}
