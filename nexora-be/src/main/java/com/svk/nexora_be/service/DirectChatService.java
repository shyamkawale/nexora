package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.DirectChatMessageRequest;
import com.svk.nexora_be.dto.response.DirectChatResponse;
import com.svk.nexora_be.dto.response.DirectChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectChatService {
    DirectChatResponse getOrCreateChat(String userId, String otherUserId);
    List<DirectChatResponse> getUserChats(String userId);
    Page<DirectChatMessageResponse> getChatMessages(String chatId, String currentUserId, Pageable pageable);
    DirectChatMessageResponse sendMessage(String userId, DirectChatMessageRequest request);
}
