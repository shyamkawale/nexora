package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.CreateGroupChatRequest;
import com.svk.nexora_be.dto.response.GroupChatResponse;
import com.svk.nexora_be.entity.GroupChat;

import java.util.List;

public interface GroupChatService {
    GroupChatResponse createGroupChat(String creatorPublicId, CreateGroupChatRequest request);
    List<GroupChatResponse> getUserGroupChats(String userPublicId);
    GroupChatResponse getGroupChatDetails(String groupChatPublicId);
    GroupChat getGroupChatById(String groupChatPublicId);
}
