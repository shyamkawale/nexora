package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.GroupChatMessageRequest;
import com.svk.nexora_be.dto.response.GroupChatMessageResponse;
import com.svk.nexora_be.entity.GroupChat;
import com.svk.nexora_be.entity.GroupChatMessage;
import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.exception.ForbiddenException;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.GroupChatRepository;
import com.svk.nexora_be.repository.GroupChatMessageRepository;
import com.svk.nexora_be.service.ChatAccessGuard;
import com.svk.nexora_be.service.GroupChatMessageService;
import com.svk.nexora_be.service.MediaFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupChatMessageServiceImpl implements GroupChatMessageService {
    private final GroupChatMessageRepository groupChatMessageRepository;
    private final GroupChatRepository groupChatRepository;
    private final ChatAccessGuard chatAccessGuard;
    private final MediaFileService mediaFileService;

    public GroupChatMessageServiceImpl(GroupChatMessageRepository groupChatMessageRepository,
                                 GroupChatRepository groupChatRepository,
                                 ChatAccessGuard chatAccessGuard,
                                 MediaFileService mediaFileService) {
        this.groupChatMessageRepository = groupChatMessageRepository;
        this.groupChatRepository = groupChatRepository;
        this.chatAccessGuard = chatAccessGuard;
        this.mediaFileService = mediaFileService;
    }

    @Override
    public Page<GroupChatMessageResponse> getGroupChatMessages(String currentUserId, String groupChatPublicId, Pageable pageable) {
        User sender = chatAccessGuard.getUserOrThrow(currentUserId);

        GroupChat chat = groupChatRepository.findByPublicId(groupChatPublicId)
                .orElseThrow(() -> new NotFoundException("Group chat not found: " + groupChatPublicId));

        chatAccessGuard.verifyGroupMembership(sender.getId(), chat.getId());

        return groupChatMessageRepository.findByGroupChatPublicIdOrderByCreatedAtDesc(groupChatPublicId, pageable)
                .map(GroupChatMessageResponse::mapGroupChatMessageToResponse);
    }

    @Override
    public GroupChatMessageResponse sendMessage(String userId, GroupChatMessageRequest request) {
        User sender = chatAccessGuard.getUserOrThrow(userId);

        GroupChat groupChat = groupChatRepository.findByPublicId(request.getChatId())
                .orElseThrow(() -> new NotFoundException("Group chat not found: " + request.getChatId()));

        chatAccessGuard.verifyGroupMembership(sender.getId(), groupChat.getId());

        MediaFile mediaFile = null;
        boolean containsMedia = request.isContainsMedia();
        if (request.getMediaFilePublicId() != null && !request.getMediaFilePublicId().isBlank()) {
            mediaFile = mediaFileService.getActiveByPublicId(request.getMediaFilePublicId());
            if (!mediaFile.getUploadedBy().getId().equals(sender.getId())) {
                throw new ForbiddenException(
                        "Cannot attach a media file uploaded by another user");
            }
            containsMedia = true;
        }

        GroupChatMessage message = GroupChatMessage.builder()
                .groupChat(groupChat)
                .sender(sender)
                .message(request.getMessage())
                .containsMedia(containsMedia)
                .mediaFile(mediaFile)
                .build();

        message = groupChatMessageRepository.save(message);

        return GroupChatMessageResponse.mapGroupChatMessageToResponse(message);
    }

    @Override
    public GroupChatMessage getMessageById(Long messageId) {
        return groupChatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }
}
