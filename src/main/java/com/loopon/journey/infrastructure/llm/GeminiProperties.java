package com.loopon.journey.infrastructure.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        String apiKey,
        String baseUrl,
        String model,
        int timeoutMs
) {
    public GeminiProperties {
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = "https://generativelanguage.googleapis.com";
        if (model == null || model.isBlank()) model = "gemini-2.5-flash";
        if (timeoutMs <= 0) timeoutMs = 10_000;
    }
}