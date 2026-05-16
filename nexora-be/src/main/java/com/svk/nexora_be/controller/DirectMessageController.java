package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageChatResponse;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.ChatBroadcaster;
import com.svk.nexora_be.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/direct-messages")
@AllArgsConstructor
public class DirectMessageController {

    private final DirectMessageService directMessageService;
    private final ChatBroadcaster chatBroadcaster;
    private final JwtUtil jwtUtil;

    @GetMapping("/chats/{otherUserId}")
    public ResponseEntity<ApiResponse<DirectMessageChatResponse>> getOrCreateChat(@PathVariable String otherUserId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        DirectMessageChatResponse chat = directMessageService.getOrCreateChat(currentUserId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success(chat, "Chat retrieved"));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<Page<DirectMessageResponse>>> getMessages(
            @PathVariable String chatId,
            Pageable pageable) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        Page<DirectMessageResponse> messages = directMessageService.getMessages(chatId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages, "Messages fetched"));
    }

    @GetMapping("/user/chats")
    public ResponseEntity<ApiResponse<List<DirectMessageChatResponse>>> getUserChats() {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        List<DirectMessageChatResponse> chats = directMessageService.getUserChats(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(chats, "Chats fetched"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DirectMessageResponse>> sendMessage(@RequestBody DirectMessageRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        DirectMessageResponse response = directMessageService.sendMessage(currentUserId, request);

        chatBroadcaster.broadcastDirectMessage(request.getChatId(), response);

        return ResponseEntity.ok(ApiResponse.success(response, "Message sent"));
    }
}
