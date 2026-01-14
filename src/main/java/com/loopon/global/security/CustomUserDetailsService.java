package com.loopon.global.security;

import com.loopon.global.security.principal.PrincipalDetails;
import com.loopon.user.domain.User;
import com.loopon.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService.loadUserByUsername - 로그인 시도: {}", email);

        User user = userRepository.findByEmail(email);

        return PrincipalDetails.from(user);
    }
}
