package com.loopon.auth.presentation;

import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.application.dto.response.LoginSuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(LoginRequest request) {
        throw new UnsupportedOperationException("이 메서드는 Spring Security의 필터에서 처리됩니다.");
    }
}
