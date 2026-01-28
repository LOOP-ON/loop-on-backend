package com.loopon.notification.infrastructure.apns;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLException;

@Configuration
public class APNsConfig {

    @Value("${apns.environment}")
    private String environment;

    @Bean
    public WebClient apnsWebClient() throws SSLException {
        String baseUrl = "sandbox".equalsIgnoreCase(environment)
                ? "https://api.sandbox.push.apple.com"
                : "https://api.push.apple.com";

        SslContext sslContext = SslContextBuilder.forClient().build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}