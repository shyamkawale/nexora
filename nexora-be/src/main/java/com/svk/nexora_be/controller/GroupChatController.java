package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreateGroupChatRequest;
import com.svk.nexora_be.dto.request.GroupChatMessageRequest;
import com.svk.nexora_be.dto.response.GroupChatResponse;
import com.svk.nexora_be.dto.response.GroupChatMessageResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.ChatBroadcaster;
import com.svk.nexora_be.service.GroupChatService;
import com.svk.nexora_be.service.GroupChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-chats")
public class GroupChatController {
    private final GroupChatService groupChatService;
    private final GroupChatMessageService groupChatMessageService;
    private final ChatBroadcaster chatBroadcaster;
    private final JwtUtil jwtUtil;

    public GroupChatController(GroupChatService groupChatService,
                             GroupChatMessageService groupChatMessageService,
                             ChatBroadcaster chatBroadcaster,
                             JwtUtil jwtUtil) {
        this.groupChatService = groupChatService;
        this.groupChatMessageService = groupChatMessageService;
        this.chatBroadcaster = chatBroadcaster;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupChatResponse>> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        String userId = jwtUtil.getCurrentUserId();

        if(request.getMemberPublicIds() == null || request.getMemberPublicIds().size() < 2) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "At least two members are required to create a group chat"));
        }

        GroupChatResponse response = groupChatService.createGroupChat(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Group chat created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupChatResponse>>> getUserGroupChats() {
        String userId = jwtUtil.getCurrentUserId();
        List<GroupChatResponse> chats = groupChatService.getUserGroupChats(userId);
        return ResponseEntity.ok(ApiResponse.success(chats, "Group chats fetched"));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<Page<GroupChatMessageResponse>>> getGroupChatMessages(
            @PathVariable String chatId,
            Pageable pageable) {
        String userId = jwtUtil.getCurrentUserId();
        Page<GroupChatMessageResponse> messages = groupChatMessageService.getGroupChatMessages(userId, chatId, pageable);
        return ResponseEntity.ok(ApiResponse.success(messages, "Group messages fetched"));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<GroupChatResponse>> getGroupChatDetails(@PathVariable String chatId) {
        GroupChatResponse response = groupChatService.getGroupChatById(chatId);
        return ResponseEntity.ok(ApiResponse.success(response, "Group chat details fetched"));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<GroupChatMessageResponse>> sendMessage(
            @PathVariable String chatId,
            @RequestBody GroupChatMessageRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        request.setChatId(chatId);
        
        GroupChatMessageResponse response = groupChatMessageService.sendMessage(userId, request);
        
        chatBroadcaster.broadcastGroupMessage(chatId, response);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Message sent"));
    }
}
