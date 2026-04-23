package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.GroupRequest;
import com.svk.nexora_be.dto.response.GroupResponse;
import com.svk.nexora_be.entity.Group;
import com.svk.nexora_be.entity.GroupMember;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.GroupMemberRepository;
import com.svk.nexora_be.repository.GroupRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    public GroupResponse createGroup(GroupRequest request, String userId) {
        User creator = userRepository.findByPublicId(userId).orElse(null);
        if (creator == null) {
            return null;
        }

        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creator(creator)
                .isActive(true)
                .build();

        group = groupRepository.save(group);

        // Add creator as ADMIN member
        GroupMember member = GroupMember.builder()
                .group(group)
                .user(creator)
                .role(GroupMember.MemberRole.ADMIN)
                .isActive(true)
                .build();
        groupMemberRepository.save(member);

        return GroupResponse.fromGroup(group);
    }

    @Override
    public GroupResponse getGroupById(String groupId) {
        Group group = groupRepository.findByPublicId(groupId).orElse(null);
        if (group != null) {
            return GroupResponse.fromGroup(group);
        }
        return null;
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupResponse::fromGroup)
                .collect(Collectors.toList());
    }

    @Override
    public void addMemberToGroup(String groupId, String userId) {
        Group group = groupRepository.findByPublicId(groupId).orElse(null);
        User user = userRepository.findByPublicId(userId).orElse(null);

        if (group != null && user != null) {
            if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
                GroupMember member = GroupMember.builder()
                        .group(group)
                        .user(user)
                        .role(GroupMember.MemberRole.MEMBER)
                        .isActive(true)
                        .build();
                groupMemberRepository.save(member);
            }
        }
    }

    @Override
    public void removeMemberFromGroup(String groupId, String userId) {
        Group group = groupRepository.findByPublicId(groupId).orElse(null);
        User user = userRepository.findByPublicId(userId).orElse(null);

        if (group != null && user != null) {
            groupMemberRepository.findByGroupAndUser(group, user)
                    .ifPresent(member -> {
                        member.setIsActive(false);
                        groupMemberRepository.save(member);
                    });
        }
    }
}
