package com.loopon.global.config;

import com.loopon.global.security.filter.JsonLoginProcessingFilter;
import com.loopon.global.security.handler.LoginFailureHandler;
import com.loopon.global.security.handler.LoginSuccessHandler;
import com.loopon.global.security.provider.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class LocalLoginSecurityConfig {
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public JsonLoginProcessingFilter jsonLoginProcessingFilter() throws Exception {
        JsonLoginProcessingFilter filter = new JsonLoginProcessingFilter(objectMapper);

        filter.setAuthenticationManager(authenticationManager());

        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(customAuthenticationProvider);
    }
}
