package com.loopon.global.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MdcLoggingFilterTest {

    @InjectMocks
    private MdcLoggingFilter mdcLoggingFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String REQUEST_ID = "request_id";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("성공 케이스: Request ID가 생성되고, 체인 실행 후 MDC가 비워져야 한다.")
    void doFilter_Success() throws ServletException, IOException {
        // Given
        doAnswer(invocation -> {
            String requestId = MDC.get(REQUEST_ID);

            assertNotNull(requestId, "Request ID가 생성되어야 합니다.");
            assertEquals(8, requestId.length(), "UUID 앞 8자리여야 합니다.");
            return null;
        }).when(filterChain).doFilter(any(), any());

        // When
        mdcLoggingFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);

        assertNull(MDC.get(REQUEST_ID), "필터 종료 후에는 MDC가 비워져야 합니다.");
    }

    @Test
    @DisplayName("예외 케이스: 필터 체인 도중 예외가 발생해도 MDC는 반드시 비워져야 한다.")
    void doFilter_Exception() throws ServletException, IOException {
        // Given
        doThrow(new RuntimeException("Unexpected Error"))
                .when(filterChain).doFilter(any(), any());

        // When & Then
        assertThrows(RuntimeException.class, () ->
                mdcLoggingFilter.doFilter(request, response, filterChain)
        );

        assertNull(MDC.get(REQUEST_ID), "예외가 발생해도 MDC는 비워져야 합니다.");
    }
}
