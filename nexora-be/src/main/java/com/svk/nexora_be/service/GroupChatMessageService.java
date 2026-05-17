package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.GroupChatMessageRequest;
import com.svk.nexora_be.dto.response.GroupChatMessageResponse;
import com.svk.nexora_be.entity.GroupChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupChatMessageService {
    Page<GroupChatMessageResponse> getGroupChatMessages(String currentUserId, String groupChatPublicId, Pageable pageable);
    GroupChatMessageResponse sendMessage(String userId, GroupChatMessageRequest request);
    GroupChatMessage getMessageById(Long messageId);
}
