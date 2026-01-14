package com.loopon.global.security.principal;

import com.loopon.global.security.dto.AuthUser;
import com.loopon.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails {
    private final AuthUser authUser;
    private final String password;

    public static PrincipalDetails from(User user) {
        AuthUser authUser = AuthUser.of(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );

        return new PrincipalDetails(authUser, user.getPassword());
    }

    public static PrincipalDetails from(AuthUser authUser) {
        return new PrincipalDetails(authUser, null);
    }

    public Long getUserId() {
        return authUser.userId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(authUser.role()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return authUser.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
