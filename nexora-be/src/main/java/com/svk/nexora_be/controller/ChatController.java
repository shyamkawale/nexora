package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.ChatRequest;
import com.svk.nexora_be.dto.response.ChatResponse;
import com.svk.nexora_be.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final OpenAIService openAIService;

    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {

        String answer = openAIService.ask(request.getMessage());

        return ResponseEntity.ok(new ChatResponse(answer));
    }
}
