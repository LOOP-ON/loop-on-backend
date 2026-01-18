package com.loopon.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopon.auth.application.dto.request.LoginRequest;
import com.loopon.auth.domain.RefreshToken;
import com.loopon.auth.infrastructure.RefreshTokenRepository;
import com.loopon.user.application.UserCommandService;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testEmail = "auth@test.com";
        testPassword = "P@ssword123!";

        // 테스트용 사용자 생성
        UserSignUpRequest signUpRequest = new UserSignUpRequest(
                testEmail,
                testPassword,
                testPassword,
                "김인증",
                "AuthUser",
                LocalDate.of(2000, 1, 1)
        );

        userCommandService.signUp(signUpRequest.toCommand());
    }

    @Nested
    @DisplayName("로그인 통합 테스트")
    class Login {

        @Test
        @DisplayName("성공: 유효한 이메일과 비밀번호로 로그인하면 토큰이 발급된다")
        void 로그인_성공() throws Exception {
            // given
            LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(cookie().exists("refresh_token"))
                    .andExpect(cookie().httpOnly("refresh_token", true));

            // RefreshToken이 Redis에 저장되었는지 확인
            assertThat(refreshTokenRepository.findById(testEmail)).isPresent();
        }

        @Test
        @DisplayName("실패: 잘못된 비밀번호로 로그인하면 401이 반환된다")
        void 로그인_실패_잘못된_비밀번호() throws Exception {
            // given
            LoginRequest loginRequest = new LoginRequest(testEmail, "WrongPassword123!");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 이메일로 로그인하면 401이 반환된다")
        void 로그인_실패_존재하지_않는_이메일() throws Exception {
            // given
            LoginRequest loginRequest = new LoginRequest("nonexistent@test.com", testPassword);

            // when & then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("토큰 재발급 통합 테스트")
    class ReissueTokens {

        private String refreshToken;

        @BeforeEach
        void setUpRefreshToken() throws Exception {
            // 로그인하여 refresh token 발급
            LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
            
            String cookieValue = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andReturn()
                    .getResponse()
                    .getCookie("refresh_token")
                    .getValue();
            
            refreshToken = cookieValue;
        }

        @Test
        @DisplayName("성공: 유효한 refresh token으로 토큰을 재발급한다")
        void 토큰_재발급_성공() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/reissue")
                            .cookie(new jakarta.servlet.http.Cookie("refresh_token", refreshToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(cookie().exists("refresh_token"));
        }

        @Test
        @DisplayName("실패: 유효하지 않은 refresh token으로 재발급하면 실패한다")
        void 토큰_재발급_실패_유효하지_않은_토큰() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/reissue")
                            .cookie(new jakarta.servlet.http.Cookie("refresh_token", "invalid_token")))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패: refresh token이 없으면 400이 반환된다")
        void 토큰_재발급_실패_토큰_없음() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/reissue"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("로그아웃 통합 테스트")
    class Logout {

        private String refreshToken;

        @BeforeEach
        void setUpRefreshToken() throws Exception {
            // 로그인하여 refresh token 발급
            LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
            
            String cookieValue = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andReturn()
                    .getResponse()
                    .getCookie("refresh_token")
                    .getValue();
            
            refreshToken = cookieValue;
        }

        @Test
        @DisplayName("성공: refresh token으로 로그아웃하면 토큰이 삭제된다")
        void 로그아웃_성공() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/logout")
                            .cookie(new jakarta.servlet.http.Cookie("refresh_token", refreshToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(cookie().maxAge("refresh_token", 0));

            // RefreshToken이 Redis에서 삭제되었는지 확인
            assertThat(refreshTokenRepository.findById(testEmail)).isEmpty();
        }

        @Test
        @DisplayName("성공: refresh token이 없어도 로그아웃이 성공한다")
        void 로그아웃_성공_토큰_없음() throws Exception {
            // when & then
            mockMvc.perform(post("/api/auth/logout"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
