package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import com.svk.nexora_be.entity.DirectMessageChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectMessageService {
    DirectMessageChat getOrCreateChat(String userId, String otherUserId);
    DirectMessageResponse sendMessage(String userId, DirectMessageRequest request);
    Page<DirectMessageResponse> getMessages(String chatId, Pageable pageable);
    List<DirectMessageChat> getUserChats(String userId);
}
