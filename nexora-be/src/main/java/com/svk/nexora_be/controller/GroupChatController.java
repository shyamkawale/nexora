package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreateGroupChatRequest;
import com.svk.nexora_be.dto.request.GroupMessageRequest;
import com.svk.nexora_be.dto.response.GroupChatResponse;
import com.svk.nexora_be.dto.response.GroupMessageResponse;
import com.svk.nexora_be.service.GroupChatService;
import com.svk.nexora_be.service.GroupMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-chats")
public class GroupChatController {
    private final GroupChatService groupChatService;
    private final GroupMessageService groupMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;

    public GroupChatController(GroupChatService groupChatService,
                             GroupMessageService groupMessageService,
                             SimpMessagingTemplate messagingTemplate,
                             JwtUtil jwtUtil) {
        this.groupChatService = groupChatService;
        this.groupMessageService = groupMessageService;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<GroupChatResponse> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        GroupChatResponse response = groupChatService.createGroupChat(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupChatResponse>> getUserGroupChats() {
        String userId = jwtUtil.getCurrentUserId();
        List<GroupChatResponse> chats = groupChatService.getUserGroupChats(userId);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<GroupChatResponse> getGroupChatDetails(@PathVariable String chatId) {
        GroupChatResponse response = groupChatService.getGroupChatDetails(chatId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<GroupMessageResponse>> getGroupMessages(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupMessageResponse> messages = groupMessageService.getGroupMessages(chatId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<GroupMessageResponse> sendMessage(
            @PathVariable String chatId,
            @RequestBody GroupMessageRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        request.setChatId(chatId);
        
        GroupMessageResponse response = groupMessageService.sendMessage(userId, request);
        
        // Broadcast message to WebSocket topic
        String topicPath = "/topic/group-messages/" + chatId;
        messagingTemplate.convertAndSend(topicPath, response);
        
        return ResponseEntity.ok(response);
    }
}
