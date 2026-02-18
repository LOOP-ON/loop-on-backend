package com.loopon.global.log;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MdcLoggingFilterTest {

    private final MdcLoggingFilter filter = new MdcLoggingFilter();

    @Test
    @DisplayName("헤더에 ID가 없으면 새로 생성하고 MDC에 넣어야 한다")
    void generateRequestId() throws Exception {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(req, res, chain);

        // then
        String requestId = res.getHeader("X-Request-ID");
        assertNotNull(requestId);
        verify(chain, times(1)).doFilter(req, res);
        assertNull(MDC.get("request_id"));
    }

    @Test
    @DisplayName("헤더에 ID가 있으면 그걸 유지해야 한다")
    void keepRequestId() throws Exception {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Request-ID", "original-id");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(req, res, chain);

        // then
        assertEquals("original-id", res.getHeader("X-Request-ID"));
    }
}
