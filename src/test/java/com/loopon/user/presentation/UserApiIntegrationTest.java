package com.loopon.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopon.user.application.UserCommandService;
import com.loopon.user.application.dto.request.UserSignUpRequest;
import com.loopon.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCommandService userCommandService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Nested
    @DisplayName("회원가입 통합 테스트")
    class SignUp {

        @Test
        @DisplayName("성공: 유효한 회원가입 요청으로 사용자가 생성된다")
        void 회원가입_성공() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(
                    "integration@test.com",
                    "P@ssword123!",
                    "P@ssword123!",
                    "김통합",
                    "IntegrationUser",
                    LocalDate.of(2000, 1, 1)
            );

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").isNumber());

            // 데이터베이스에 실제로 저장되었는지 확인
            assertThat(userRepository.existsByEmail("integration@test.com")).isTrue();
        }

        @Test
        @DisplayName("실패: 비밀번호와 확인 비밀번호가 다르면 회원가입이 실패한다")
        void 회원가입_실패_비밀번호_불일치() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(
                    "integration2@test.com",
                    "P@ssword123!",
                    "Different123!",
                    "김통합",
                    "IntegrationUser2",
                    LocalDate.of(2000, 1, 1)
            );

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));

            // 데이터베이스에 저장되지 않았는지 확인
            assertThat(userRepository.existsByEmail("integration2@test.com")).isFalse();
        }

        @Test
        @DisplayName("실패: 이미 존재하는 이메일로 회원가입하면 실패한다")
        void 회원가입_실패_이메일_중복() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(
                    "duplicate@test.com",
                    "P@ssword123!",
                    "P@ssword123!",
                    "김통합",
                    "FirstUser",
                    LocalDate.of(2000, 1, 1)
            );

            // 첫 번째 회원가입 성공
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // when & then - 두 번째 회원가입 시도 (같은 이메일)
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("실패: 유효하지 않은 이메일 형식이면 회원가입이 실패한다")
        void 회원가입_실패_유효하지_않은_이메일() throws Exception {
            // given
            UserSignUpRequest request = new UserSignUpRequest(
                    "invalid-email",
                    "P@ssword123!",
                    "P@ssword123!",
                    "김통합",
                    "IntegrationUser3",
                    LocalDate.of(2000, 1, 1)
            );

            // when & then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("이메일 중복 확인 통합 테스트")
    class CheckEmail {

        @Test
        @DisplayName("성공: 사용 가능한 이메일을 확인하면 true를 반환한다")
        void 이메일_중복_확인_사용_가능() throws Exception {
            // when & then
            mockMvc.perform(post("/api/users/check-email")
                            .param("email", "available@test.com"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(true));
        }

        @Test
        @DisplayName("성공: 이미 사용 중인 이메일을 확인하면 false를 반환한다")
        void 이메일_중복_확인_이미_사용_중() throws Exception {
            // given - 회원가입으로 이메일 등록
            UserSignUpRequest signUpRequest = new UserSignUpRequest(
                    "check@test.com",
                    "P@ssword123!",
                    "P@ssword123!",
                    "김통합",
                    "CheckUser",
                    LocalDate.of(2000, 1, 1)
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpRequest)))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(post("/api/users/check-email")
                            .param("email", "check@test.com"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(false));
        }
    }

    @Nested
    @DisplayName("닉네임 중복 확인 통합 테스트")
    class CheckNickname {

        @Test
        @DisplayName("성공: 사용 가능한 닉네임을 확인하면 true를 반환한다")
        void 닉네임_중복_확인_사용_가능() throws Exception {
            // when & then
            mockMvc.perform(post("/api/users/check-nickname")
                            .param("nickname", "AvailableNickname"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(true));
        }

        @Test
        @DisplayName("성공: 이미 사용 중인 닉네임을 확인하면 false를 반환한다")
        void 닉네임_중복_확인_이미_사용_중() throws Exception {
            // given - 회원가입으로 닉네임 등록
            UserSignUpRequest signUpRequest = new UserSignUpRequest(
                    "nickname@test.com",
                    "P@ssword123!",
                    "P@ssword123!",
                    "김통합",
                    "UsedNickname",
                    LocalDate.of(2000, 1, 1)
            );

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signUpRequest)))
                    .andExpect(status().isOk());

            // when & then
            mockMvc.perform(post("/api/users/check-nickname")
                            .param("nickname", "UsedNickname"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(false));
        }
    }
}