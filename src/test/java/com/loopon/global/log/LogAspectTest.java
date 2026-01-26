package com.loopon.global.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class LogAspectTest {

    @InjectMocks
    private LogAspect logAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Test
    @DisplayName("성공: 정상 수행 시 파라미터, 결과값, 소요시간이 로그에 남아야 한다")
    void 정상_수행_로그_기록(CapturedOutput output) throws Throwable {
        // Given
        setupJoinPoint(UserCommandService.class, "signUp",
                new String[]{"email", "nickname"},
                new Object[]{"test@test.com", "tester"});

        given(joinPoint.proceed()).willReturn("SuccessResult");

        // When
        logAspect.logExecution(joinPoint);

        // Then
        verify(joinPoint, times(1)).proceed();

        assertThat(output.getOut())
                .contains("UserCommandService")
                .contains("signUp")
                .contains("email=test@test.com")
                .contains("SuccessResult")
                .containsPattern("\\[\\d+ms\\]");
    }

    @Test
    @DisplayName("마스킹: 민감한 키워드(password, token 등)는 ****로 가려져야 한다")
    void 민감_정보_마스킹_처리(CapturedOutput output) throws Throwable {
        // Given
        setupJoinPoint(AuthService.class, "login",
                new String[]{"email", "password", "refreshToken"},
                new Object[]{"user@test.com", "secret1234", "eyJh..."});

        given(joinPoint.proceed()).willReturn("LoginSuccess");

        // When
        logAspect.logExecution(joinPoint);

        // Then
        assertThat(output.getOut())
                .contains("email=user@test.com")
                .doesNotContain("secret1234")
                .doesNotContain("eyJh...")
                .contains("password=****")
                .contains("refreshToken=****");
    }

    @Test
    @DisplayName("예외: 예외 발생 시 에러 로그가 남고 예외가 다시 던져져야 한다")
    void 예외_발생_시_에러_로그_기록(CapturedOutput output) throws Throwable {
        // Given
        setupJoinPoint(UserQueryService.class, "getUser",
                new String[]{"userId"},
                new Object[]{1L});

        given(joinPoint.proceed()).willThrow(new IllegalArgumentException("Invalid User ID"));

        // When & Then
        assertThatThrownBy(() -> logAspect.logExecution(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid User ID");

        assertThat(output.getOut())
                .contains("[EXCEPTION]")
                .contains("IllegalArgumentException")
                .contains("Invalid User ID")
                .contains("userId=1");
    }

    @Test
    @DisplayName("말줄임: 결과값이 100자를 넘으면 잘라서(...) 로깅해야 한다")
    void 긴_결과값_말줄임_처리(CapturedOutput output) throws Throwable {
        // Given
        setupJoinPoint(BoardService.class, "getContent", new String[]{}, new Object[]{});

        String longString = "A".repeat(150);
        given(joinPoint.proceed()).willReturn(longString);

        // When
        logAspect.logExecution(joinPoint);

        // Then
        assertThat(output.getOut())
                .contains(longString.substring(0, 100) + "...")
                .doesNotContain(longString);
    }

    private void setupJoinPoint(Class<?> targetClass, String methodName, String[] paramNames, Object[] args) {
        doReturn(targetClass).when(methodSignature).getDeclaringType();

        given(methodSignature.getName()).willReturn(methodName);
        given(methodSignature.getParameterNames()).willReturn(paramNames);

        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(joinPoint.getArgs()).willReturn(args);
    }

    interface UserCommandService {}
    interface AuthService {}
    interface UserQueryService {}
    interface BoardService {}
}
