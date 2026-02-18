package com.loopon.global.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LogAspectTest {

    @InjectMocks
    private LogAspect logAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @Test
    @DisplayName("결과값이 100자를 넘으면 잘라야 한다")
    void truncateResult() throws Throwable {
        // given
        String longString = "A".repeat(200);
        given(joinPoint.proceed()).willReturn(longString);

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getDeclaringType()).willReturn(TestController.class);
        given(signature.getName()).willReturn("testMethod");
        given(joinPoint.getArgs()).willReturn(new Object[]{});

        // when
        Object result = logAspect.logExecution(joinPoint);

        // then
        assertEquals(longString, result);
    }

    static class TestController {
    }
}
