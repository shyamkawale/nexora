package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import com.svk.nexora_be.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/direct-messages")
@AllArgsConstructor
public class DirectMessageController {

    private final DirectMessageService directMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    @GetMapping("/chats/{otherUserId}")
    public ResponseEntity<?> getOrCreateChat(@PathVariable String otherUserId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        var chat = directMessageService.getOrCreateChat(currentUserId, otherUserId);
        if (chat != null) {
            return ResponseEntity.ok(chat);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Page<DirectMessageResponse>> getMessages(
            @PathVariable String chatId,
            Pageable pageable) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        Page<DirectMessageResponse> messages = directMessageService.getMessages(chatId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/chats")
    public ResponseEntity<?> getUserChats() {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        var chats = directMessageService.getUserChats(currentUserId);
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<DirectMessageResponse> sendMessage(@RequestBody DirectMessageRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();

        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        DirectMessageResponse response = directMessageService.sendMessage(currentUserId, request);
        
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }

        // Broadcast message to WebSocket subscribers
        String topicPath = "/topic/messages/" + request.getChatId();
        messagingTemplate.convertAndSend(topicPath, response);

        return ResponseEntity.ok(response);
    }
}
