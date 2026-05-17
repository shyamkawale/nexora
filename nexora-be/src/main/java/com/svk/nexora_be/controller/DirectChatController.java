package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.DirectChatMessageRequest;
import com.svk.nexora_be.dto.response.DirectChatResponse;
import com.svk.nexora_be.dto.response.DirectChatMessageResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.ChatBroadcaster;
import com.svk.nexora_be.service.DirectChatService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/direct-chats")
@AllArgsConstructor
public class DirectChatController {

    private final DirectChatService directChatService;
    private final ChatBroadcaster chatBroadcaster;
    private final JwtUtil jwtUtil;

    @PostMapping("/chats/{otherUserId}")
    public ResponseEntity<ApiResponse<DirectChatResponse>> getOrCreateChat(@PathVariable String otherUserId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        DirectChatResponse chat = directChatService.getOrCreateChat(currentUserId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success(chat, "Chat retrieved"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DirectChatResponse>>> getUserChats() {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        List<DirectChatResponse> chats = directChatService.getUserChats(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(chats, "Chats fetched"));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<Page<DirectChatMessageResponse>>> getChatMessages(
            @PathVariable String chatId,
            Pageable pageable) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        Page<DirectChatMessageResponse> messages = directChatService.getChatMessages(chatId, currentUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages fetched"));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<DirectChatMessageResponse>> sendMessage(
            @PathVariable String chatId,
            @RequestBody DirectChatMessageRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();
        request.setChatId(chatId);

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        DirectChatMessageResponse response = directChatService.sendMessage(currentUserId, request);

        chatBroadcaster.broadcastDirectMessage(chatId, response);

        return ResponseEntity.ok(ApiResponse.success(response, "Message sent"));
    }
}
