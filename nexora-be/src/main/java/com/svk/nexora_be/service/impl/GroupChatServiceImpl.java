package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.CreateGroupChatRequest;
import com.svk.nexora_be.dto.response.GroupChatResponse;
import com.svk.nexora_be.entity.GroupChat;
import com.svk.nexora_be.entity.GroupChatMember;
import com.svk.nexora_be.enums.GroupChatMemberRole;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.GroupChatMemberRepository;
import com.svk.nexora_be.repository.GroupChatRepository;
import com.svk.nexora_be.service.ChatAccessGuard;
import com.svk.nexora_be.service.GroupChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupChatServiceImpl implements GroupChatService {
    private final GroupChatRepository groupChatRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;
    private final ChatAccessGuard chatAccessGuard;

    public GroupChatServiceImpl(GroupChatRepository groupChatRepository,
                              GroupChatMemberRepository groupChatMemberRepository,
                              ChatAccessGuard chatAccessGuard) {
        this.groupChatRepository = groupChatRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
        this.chatAccessGuard = chatAccessGuard;
    }

    @Override
    public GroupChatResponse createGroupChat(String creatorPublicId, CreateGroupChatRequest request) {
        User creator = chatAccessGuard.getUserOrThrow(creatorPublicId);

        GroupChat groupChat = GroupChat.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .createdBy(creator)
                .members(new ArrayList<>())
                .build();

        groupChat = groupChatRepository.save(groupChat);

        // Add creator as member (ADMIN)
        GroupChatMember creatorMember = GroupChatMember.builder()
                .groupChat(groupChat)
                .user(creator)
                .role(GroupChatMemberRole.ADMIN)
                .isActive(true)
                .build();
        groupChatMemberRepository.save(creatorMember);
        groupChat.getMembers().add(creatorMember);

        // Add other members
        if (request.getMemberPublicIds() != null) {
            for (String memberPublicId : request.getMemberPublicIds()) {
                User member = chatAccessGuard.getUserOrThrow(memberPublicId);
                
                GroupChatMember groupChatMember = GroupChatMember.builder()
                        .groupChat(groupChat)
                        .user(member)
                        .role(GroupChatMemberRole.MEMBER)
                        .isActive(true)
                        .build();
                groupChatMemberRepository.save(groupChatMember);
                groupChat.getMembers().add(groupChatMember);
            }
        }

        return GroupChatResponse.mapGroupChatToResponse(groupChat);
    }

    @Override
    public List<GroupChatResponse> getUserGroupChats(String userPublicId) {
        chatAccessGuard.getUserOrThrow(userPublicId);
        List<GroupChat> groupChats = groupChatRepository.findActiveGroupsForUser(userPublicId);
        return groupChats.stream()
                .map(GroupChatResponse::mapGroupChatToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GroupChatResponse getGroupChatById(String groupChatPublicId) {
        GroupChat groupChat = groupChatRepository.findByPublicId(groupChatPublicId)
                .orElseThrow(() -> new NotFoundException("Group chat not found: " + groupChatPublicId));
        return GroupChatResponse.mapGroupChatToResponse(groupChat);
    }
}
