package com.loopon.global.security.filter;

import com.loopon.auth.application.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/auth/login";
    private static final String HTTP_METHOD = "POST";

    public JsonLoginProcessingFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_REQUEST_URL);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!request.getMethod().equals(HTTP_METHOD)) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
