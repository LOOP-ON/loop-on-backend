package com.loopon.auth.infrastructure.apple;

import com.loopon.auth.infrastructure.apple.dto.AppleTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class AppleAuthClientAdapterTest {

    private AppleAuthClientAdapter adapter;
    private MockRestServiceServer mockServer;

    @Mock
    private AppleClientSecretGenerator secretGenerator;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        adapter = new AppleAuthClientAdapter(restClient, secretGenerator);

        ReflectionTestUtils.setField(adapter, "clientId", "com.loopon.app");
    }

    @Test
    @DisplayName("성공: 애플 토큰을 정상적으로 발급받는다")
    void 애플_토큰_정상_발급() {
        // given
        String authCode = "test_auth_code";
        String mockClientSecret = "mock_jwt_secret";

        given(secretGenerator.createClientSecret()).willReturn(mockClientSecret);

        String responseBody = """
                {
                    "access_token": "apple_access_token",
                    "token_type": "Bearer",
                    "expires_in": 3600,
                    "refresh_token": "apple_refresh_token",
                    "id_token": "apple_id_token"
                }
                """;

        // when & then
        mockServer.expect(requestTo("https://appleid.apple.com/auth/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().formDataContains(java.util.Map.of(
                        "client_id", "com.loopon.app",
                        "client_secret", mockClientSecret,
                        "code", authCode,
                        "grant_type", "authorization_code"
                )))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        AppleTokenResponse result = adapter.getTokens(authCode);

        assertThat(result).isNotNull();

        mockServer.verify();
    }
}
