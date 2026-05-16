package com.svk.nexora_be.service;

import com.svk.nexora_be.entity.GroupChatMember;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.exception.ForbiddenException;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.GroupChatMemberRepository;
import com.svk.nexora_be.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatAccessGuard {
    private final UserRepository userRepository;
    private final GroupChatMemberRepository groupChatMemberRepository;

    public ChatAccessGuard(UserRepository userRepository, 
                          GroupChatMemberRepository groupChatMemberRepository) {
        this.userRepository = userRepository;
        this.groupChatMemberRepository = groupChatMemberRepository;
    }

    public User getUserOrThrow(String userPublicId) {
        return userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userPublicId));
    }

    public void verifyGroupMembership(Long userId, Long groupChatId) {
        GroupChatMember membership = groupChatMemberRepository
                .findByGroupChatIdAndUserId(groupChatId, userId)
                .orElseThrow(() -> new ForbiddenException("User is not a member of this group"));

        if (!membership.getIsActive()) {
            throw new ForbiddenException("User membership is not active");
        }
    }
}
