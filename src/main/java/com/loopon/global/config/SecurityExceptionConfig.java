package com.loopon.global.config;

import com.loopon.global.security.handler.CustomAccessDeniedHandler;
import com.loopon.global.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityExceptionConfig {
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public void configure(ExceptionHandlingConfigurer<HttpSecurity> ex) {
        ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);
    }
}
