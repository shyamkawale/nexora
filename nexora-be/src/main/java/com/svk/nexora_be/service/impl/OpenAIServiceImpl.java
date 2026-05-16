package com.svk.nexora_be.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svk.nexora_be.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * OpenAI Service Implementation with RAG (Retrieval Augmented Generation) support
 * Sends prompts to OpenAI API and extracts responses
 */
@Service
public class OpenAIServiceImpl implements OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIServiceImpl.class);
    
    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIServiceImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @Override
    public String ask(String userMessage) {
        // Simple question without knowledge context
        String prompt = String.format("You are a helpful assistant for the Nexora collaboration platform. Answer the following question:\n\nQuestion: %s", userMessage);
        return sendToOpenAI(prompt);
    }
    
    @Override
    public String askWithContext(String userMessage, String knowledgeContext) {
        // Question with knowledge base context (RAG)
        String prompt = String.format(
            "You are the Nexora AI Assistant. You have access to Nexora's documentation. " +
            "Answer the following question using ONLY the knowledge base provided below. " +
            "If the answer is not in the knowledge base, clearly state that you don't have that information.\n\n" +
            "=== KNOWLEDGE BASE ===\n%s\n\n" +
            "=== USER QUESTION ===\n%s\n\n" +
            "=== ANSWER ===",
            knowledgeContext,
            userMessage
        );
        return sendToOpenAI(prompt);
    }
    
    /**
     * Send prompt to OpenAI and get response
     */
    private String sendToOpenAI(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1-mini",
                    "input", prompt
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
        } catch (Exception e) {
            logger.error("Error calling OpenAI API", e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }

    private String extractAssistantText(String rawJson) {
        if (rawJson == null || rawJson.isEmpty()) {
            logger.warn("Empty response from OpenAI");
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
            logger.error("Failed to parse OpenAI response", ex);
            return "";
        }
    }
}
