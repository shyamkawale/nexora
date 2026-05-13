package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {
    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient webClient;

    public OpenAIServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public String ask(String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4.1-mini", // gpt-5",
                "input", userMessage
        );

        return webClient.post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
