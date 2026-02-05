package com.loopon.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private static final String JWT_SCHEME_NAME = "JWT-Auth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement())
                .components(components())
                .servers(servers());
    }

    @Bean
    public GroupedOpenApi allApi() {
        return createGroup("All API", "/**");
    }

    @Bean
    public GroupedOpenApi userApi() {
        return createGroup("1. 사용자(User)", "/api/users/**");
    }

    @Bean
    public GroupedOpenApi authApi() {
        return createGroup("2. 인증(Auth)", "/api/auth/**");
    }

    @Bean
    public GroupedOpenApi termApi() {
        return createGroup("3. 약관(Term)", "/api/terms/**");
    }

    @Bean
    public GroupedOpenApi friendApi() {
        return createGroup("4. 친구(Friend)", "/api/friend/**");
    }

    @Bean
    public GroupedOpenApi friendRequestApi() {
        return createGroup("5. 친구요청(FriendRequest)", "/api/friend-request/**");
    }

    @Bean
    public GroupedOpenApi journeyApi() {
        return createGroup("6. 여정(Journey)", "/api/journeys/**");
    }

    @Bean
    public GroupedOpenApi routineApi() {
        return createGroup("7. 루틴(Routine)", "/api/routines/**");
    }

    private Info apiInfo() {
        return new Info()
                .title("Loop:ON API Document")
                .version("v1.0")
                .description("""
                        Loop:ON 프로젝트의 REST API 명세서입니다.
                        
                        **[인증 가이드]**
                        - Access Token: 우측 상단 `Authorize` 버튼 -> `Bearer {token}` 입력
                        - Refresh Token: `httpOnly` 쿠키로 자동 관리됨
                        """);
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
                        .name(JWT_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Access Token 입력")
                );
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(JWT_SCHEME_NAME);
    }

    private List<Server> servers() {
        return List.of(
                new Server().url("http://localhost:8080").description("Local Server")
                // TODO 배포 환경 등록
        );
    }

    private GroupedOpenApi createGroup(String groupName, String... paths) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .pathsToMatch(paths)
                .build();
    }
}
