package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.GroupMessageRequest;
import com.svk.nexora_be.dto.response.GroupMessageResponse;
import com.svk.nexora_be.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupMessageService {
    GroupMessageResponse sendMessage(String userId, GroupMessageRequest request);
    Page<GroupMessageResponse> getGroupMessages(String groupChatPublicId, Pageable pageable);
    GroupMessage getMessageById(Long messageId);
}
