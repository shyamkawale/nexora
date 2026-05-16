package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.ChatRequest;
import com.svk.nexora_be.dto.response.ChatResponse;
import com.svk.nexora_be.service.KnowledgeBaseService;
import com.svk.nexora_be.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat Controller for AI Chatbot with RAG (Retrieval Augmented Generation)
 * Handles user questions and returns AI-generated responses based on knowledge base
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final OpenAIService openAIService;
    private final KnowledgeBaseService knowledgeBaseService;

    public ChatController(OpenAIService openAIService, KnowledgeBaseService knowledgeBaseService) {
        this.openAIService = openAIService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * Send a message to the AI chatbot
     * Uses RAG to augment the prompt with relevant knowledge base documents
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        // Build context from knowledge base
        String knowledgeContext = knowledgeBaseService.buildAugmentedContext(request.getMessage());
        
        // Ask AI with context
        String answer;
        if (!knowledgeContext.isEmpty()) {
            answer = openAIService.askWithContext(request.getMessage(), knowledgeContext);
        } else {
            // Fallback to simple ask if no knowledge context found
            answer = openAIService.ask(request.getMessage());
        }

        return ResponseEntity.ok(new ChatResponse(answer));
    }
}
