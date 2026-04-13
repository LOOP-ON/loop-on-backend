package com.loopon.user.infrastructure.persistence.jpa;

import com.loopon.global.domain.BaseTimeEntity;
import com.loopon.user.domain.enums.UserProvider;
import com.loopon.user.domain.enums.UserRole;
import com.loopon.user.domain.enums.UserStatus;
import com.loopon.user.domain.enums.UserVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users",
        //제약조건 추가
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_users_email", columnNames = "email"),
                @UniqueConstraint(name = "ux_users_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "ux_users_provider_social_id", columnNames = {"provider", "social_id"}),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20, nullable = false)
    private UserProvider provider;

    @Column(name = "social_id", length = 200, nullable = false)
    private String socialId;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "nickname", length = 30, nullable = false)
    private String nickname;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", length = 20, nullable = false)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private UserRole role;

    @Column(name = "bio", length = 100)
    private String bio;

    @Column(name = "status_message", length = 100)
    private String statusMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @Builder.Default
    private UserVisibility visibility = UserVisibility.PUBLIC;

    public String getUserRole() {
        return this.role.name();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String nickname, String bio, String statusMessage, String profileImageUrl, UserVisibility visibility) {
        this.nickname = nickname;
        this.bio = bio;
        this.statusMessage = statusMessage;
        this.profileImageUrl = profileImageUrl;
        this.visibility = visibility;
    }

    public static UserJpaEntity createLocalUser(String email, String nickname, String encodedPassword, String profileImageUrl) {
        return UserJpaEntity.builder()
                .provider(UserProvider.LOCAL)
                .socialId(email)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .userStatus(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .bio("")
                .statusMessage("")
                .build();
    }

    public static UserJpaEntity createSocialUser(String socialId, UserProvider provider, String email, String nickname, String profileImageUrl) {
        return UserJpaEntity.builder()
                .provider(provider)
                .socialId(socialId)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .userStatus(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .bio("")
                .statusMessage("")
                .build();
    }
}
