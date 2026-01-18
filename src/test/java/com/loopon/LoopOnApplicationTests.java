package com.loopon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("local")
class LoopOnApplicationTests {

    @Test
    @DisplayName("애플리케이션 컨텍스트가 정상적으로 로드된다")
    void contextLoads() {
        // Spring Boot 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
        // 모든 Bean이 정상적으로 주입되고 설정이 올바른지 검증
    }
}
