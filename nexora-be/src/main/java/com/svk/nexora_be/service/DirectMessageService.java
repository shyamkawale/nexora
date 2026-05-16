package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageChatResponse;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectMessageService {
    DirectMessageChatResponse getOrCreateChat(String userId, String otherUserId);
    DirectMessageResponse sendMessage(String userId, DirectMessageRequest request);
    Page<DirectMessageResponse> getMessages(String chatId, Pageable pageable);
    List<DirectMessageChatResponse> getUserChats(String userId);
}
