package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.GroupMessageRequest;
import com.svk.nexora_be.dto.response.GroupMessageResponse;
import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.GroupChat;
import com.svk.nexora_be.entity.GroupMessage;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.GroupChatMemberRepository;
import com.svk.nexora_be.repository.GroupChatRepository;
import com.svk.nexora_be.repository.GroupMessageRepository;
import com.svk.nexora_be.service.ChatAccessGuard;
import com.svk.nexora_be.service.GroupMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupMessageServiceImpl implements GroupMessageService {
    private final GroupMessageRepository groupMessageRepository;
    private final GroupChatRepository groupChatRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;
    private final ChatAccessGuard chatAccessGuard;

    public GroupMessageServiceImpl(GroupMessageRepository groupMessageRepository,
                                 GroupChatRepository groupChatRepository,
                                 GroupChatMemberRepository groupChatMemberRepository,
                                 ChatAccessGuard chatAccessGuard) {
        this.groupMessageRepository = groupMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
        this.chatAccessGuard = chatAccessGuard;
    }

    @Override
    public GroupMessageResponse sendMessage(String userId, GroupMessageRequest request) {
        User sender = chatAccessGuard.getUserOrThrow(userId);

        GroupChat groupChat = groupChatRepository.findByPublicId(request.getChatId())
                .orElseThrow(() -> new NotFoundException("Group chat not found: " + request.getChatId()));

        chatAccessGuard.verifyGroupMembership(sender.getId(), groupChat.getId());

        GroupMessage message = GroupMessage.builder()
                .groupChat(groupChat)
                .sender(sender)
                .message(request.getMessage())
                .containsMedia(false)
                .build();

        message = groupMessageRepository.save(message);

        return mapToResponse(message);
    }

    @Override
    public Page<GroupMessageResponse> getGroupMessages(String groupChatPublicId, Pageable pageable) {
        groupChatRepository.findByPublicId(groupChatPublicId)
                .orElseThrow(() -> new NotFoundException("Group chat not found: " + groupChatPublicId));

        return groupMessageRepository.findByGroupChatPublicIdOrderByCreatedAtDesc(groupChatPublicId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public GroupMessage getMessageById(Long messageId) {
        return groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }

    private GroupMessageResponse mapToResponse(GroupMessage message) {
        return GroupMessageResponse.builder()
                .publicId(message.getPublicId())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .sender(UserResponse.builder()
                        .publicId(message.getSender().getPublicId())
                        .username(message.getSender().getUsername())
                        .email(message.getSender().getEmail())
                        .profilePicture(message.getSender().getProfilePicture())
                        .bio(message.getSender().getBio())
                        .build())
                .groupChatId(message.getGroupChat().getPublicId())
                .build();
    }
}
