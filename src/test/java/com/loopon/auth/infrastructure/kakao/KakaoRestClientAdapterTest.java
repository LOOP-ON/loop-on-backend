package com.loopon.auth.infrastructure.kakao;

import com.loopon.auth.infrastructure.kakao.dto.KakaoUserResponse;
import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoRestClientAdapterTest {
    private KakaoRestClientAdapter adapter;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();

        mockServer = MockRestServiceServer.bindTo(builder).build();

        adapter = new KakaoRestClientAdapter(builder.build());
    }

    @Test
    @DisplayName("성공: 사용자 정보를 정상적으로 가져온다")
    void 사용자_정보_조회_성공() {
        // given
        String accessToken = "test_access_token";

        String responseBody = """
                {
                    "id": 123456789,
                    "connected_at": "2023-01-01T00:00:00Z",
                    "kakao_account": { 
                        "email": "test@kakao.com",
                        "profile": { "nickname": "홍길동" }
                    }
                }
                """;

        // when & then
        mockServer.expect(requestTo("/v2/user/me"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + accessToken))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        KakaoUserResponse result = adapter.getUserInfo(accessToken);

        assertThat(result).isNotNull();

        mockServer.verify();
    }

    @Test
    @DisplayName("실패: 4xx 에러 발생 시 SOCIAL_LOGIN_FAILED 예외를 던진다")
    void 사용자_정보_조회_실패_4xx_에러() {
        // given
        String accessToken = "invalid_token";

        // when & then
        mockServer.expect(requestTo("/v2/user/me"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> adapter.getUserInfo(accessToken))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SOCIAL_LOGIN_FAILED);
    }

    @Test
    @DisplayName("실패: 5xx 에러 발생 시 EXTERNAL_SERVER_ERROR 예외를 던진다")
    void 사용자_정보_조회_실패_5xx_에러() {
        // given
        String accessToken = "valid_token";

        // when & then
        mockServer.expect(requestTo("/v2/user/me"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> adapter.getUserInfo(accessToken))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXTERNAL_SERVER_ERROR);
    }
}