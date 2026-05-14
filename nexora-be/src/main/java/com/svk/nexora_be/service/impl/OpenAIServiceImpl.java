package com.svk.nexora_be.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public String ask(String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4.1-mini", // gpt-5",
                "input", userMessage
        );

        String rawJson = webClient.post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractAssistantText(rawJson);
    }

    private String extractAssistantText(String rawJson) {
        if (rawJson == null || rawJson.isEmpty()) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(rawJson);

            return root
                    .path("output")
                    .get(0)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse OpenAI response", ex);
        }
    }
}
