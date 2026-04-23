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
        System.out.println("📞 GetOrCreateChat - CurrentUserId: " + currentUserId + ", OtherUserId: " + otherUserId);
        if (currentUserId == null) {
            System.out.println("❌ No current user - returning 401");
            return ResponseEntity.status(401).build();
        }

        var chat = directMessageService.getOrCreateChat(currentUserId, otherUserId);
        if (chat != null) {
            System.out.println("✅ Chat found/created: " + chat);
            return ResponseEntity.ok(chat);
        }
        System.out.println("❌ Chat not found - returning 404");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Page<DirectMessageResponse>> getMessages(
            @PathVariable String chatId,
            Pageable pageable) {
        String currentUserId = jwtUtil.getCurrentUserId();
        System.out.println("💬 GetMessages - CurrentUserId: " + currentUserId + ", ChatId: " + chatId);
        if (currentUserId == null) {
            System.out.println("❌ No current user - returning 401");
            return ResponseEntity.status(401).build();
        }

        Page<DirectMessageResponse> messages = directMessageService.getMessages(chatId, pageable);
        System.out.println("✅ Messages fetched: " + messages.getTotalElements() + " messages");
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/chats")
    public ResponseEntity<?> getUserChats() {
        String currentUserId = jwtUtil.getCurrentUserId();
        System.out.println("📋 GetUserChats - CurrentUserId: " + currentUserId);
        if (currentUserId == null) {
            System.out.println("❌ No current user - returning 401");
            return ResponseEntity.status(401).build();
        }

        var chats = directMessageService.getUserChats(currentUserId);
        System.out.println("✅ User chats fetched: " + (chats != null ? chats.size() : 0) + " chats");
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<DirectMessageResponse> sendMessage(@RequestBody DirectMessageRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();
        System.out.println("💬 SendMessage - CurrentUserId: " + currentUserId + ", ChatId: " + request.getChatId());
        
        if (currentUserId == null) {
            System.out.println("❌ No current user - returning 401");
            return ResponseEntity.status(401).build();
        }

        DirectMessageResponse response = directMessageService.sendMessage(currentUserId, request);
        
        if (response == null) {
            System.out.println("❌ Failed to send message");
            return ResponseEntity.badRequest().build();
        }

        System.out.println("✅ Message sent: " + response.getPublicId());
        
        // Broadcast message to WebSocket subscribers
        String topicPath = "/topic/messages/" + request.getChatId();
        System.out.println("📡 Broadcasting to: " + topicPath);
        messagingTemplate.convertAndSend(topicPath, response);

        return ResponseEntity.ok(response);
    }
}
