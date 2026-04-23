package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.GroupMessageRequest;
import com.svk.nexora_be.dto.response.GroupMessageResponse;
import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.GroupChat;
import com.svk.nexora_be.entity.GroupChatMember;
import com.svk.nexora_be.entity.GroupMessage;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.GroupChatMemberRepository;
import com.svk.nexora_be.repository.GroupChatRepository;
import com.svk.nexora_be.repository.GroupMessageRepository;
import com.svk.nexora_be.repository.UserRepository;
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
    private final UserRepository userRepository;

    public GroupMessageServiceImpl(GroupMessageRepository groupMessageRepository,
                                 GroupChatRepository groupChatRepository,
                                 GroupChatMemberRepository groupChatMemberRepository,
                                 UserRepository userRepository) {
        this.groupMessageRepository = groupMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GroupMessageResponse sendMessage(String userId, GroupMessageRequest request) {
        // Get sender
        User sender = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Get group chat
        GroupChat groupChat = groupChatRepository.findByPublicId(request.getChatId())
                .orElseThrow(() -> new RuntimeException("Group chat not found"));

        // Verify user is member of group
        GroupChatMember membership = groupChatMemberRepository
                .findByGroupChatAndUser(groupChat, sender)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (!membership.getIsActive()) {
            throw new RuntimeException("User membership is not active");
        }

        // Create and save message
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
        // Verify group exists
        groupChatRepository.findByPublicId(groupChatPublicId)
                .orElseThrow(() -> new RuntimeException("Group chat not found"));

        return groupMessageRepository.findByGroupChatPublicIdOrderByCreatedAtDesc(groupChatPublicId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public GroupMessage getMessageById(Long messageId) {
        return groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    private GroupMessageResponse mapToResponse(GroupMessage message) {
        return GroupMessageResponse.builder()
                .publicId(message.getId().toString())
                .message(message.getMessage())
                .createdAt(java.sql.Timestamp.valueOf(message.getCreatedAt()).getTime())
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
