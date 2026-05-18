package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.DirectChatMessageRequest;
import com.svk.nexora_be.dto.response.DirectChatResponse;
import com.svk.nexora_be.dto.response.DirectChatMessageResponse;
import com.svk.nexora_be.entity.DirectChatMessage;
import com.svk.nexora_be.entity.DirectChat;
import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.exception.ForbiddenException;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.DirectChatRepository;
import com.svk.nexora_be.repository.DirectChatMessageRepository;
import com.svk.nexora_be.repository.OrganizationRepository;
import com.svk.nexora_be.service.ChatAccessGuard;
import com.svk.nexora_be.service.DirectChatService;
import com.svk.nexora_be.service.MediaFileService;
import com.svk.nexora_be.tenant.OrganizationContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectChatServiceImpl implements DirectChatService {

    private final DirectChatMessageRepository directChatMessageRepository;
    private final DirectChatRepository directChatRepository;
    private final ChatAccessGuard chatAccessGuard;
    private final MediaFileService mediaFileService;
    private final OrganizationRepository organizationRepository;

    @Override
    public DirectChatResponse getOrCreateChat(String userId, String otherUserId) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        User user = chatAccessGuard.getUserOrThrow(userId);
        User otherUser = chatAccessGuard.getUserOrThrow(otherUserId);
        chatAccessGuard.verifyApprovedOrganizationMember(organizationId, otherUserId);

        DirectChat chat = directChatRepository.findChatBetweenUsersInOrganization(organizationId, user, otherUser)
                .orElseGet(() -> {
                    DirectChat newChat = DirectChat.builder()
                            .organization(organizationRepository.getReferenceById(organizationId))
                            .user1(user)
                            .user2(otherUser)
                            .build();
                    return directChatRepository.save(newChat);
                });
        
        return DirectChatResponse.mapDirectChatToResponse(chat);
    }

    @Override
    public List<DirectChatResponse> getUserChats(String userId) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        User user = chatAccessGuard.getUserOrThrow(userId);
        return directChatRepository.findAllChatsForUserInOrganization(organizationId, user)
                .stream()
                .map(DirectChatResponse::mapDirectChatToResponse)
                .toList();
    }

    @Override
    public Page<DirectChatMessageResponse> getChatMessages(String chatId, String currentUserId, Pageable pageable) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        DirectChat chat = directChatRepository.findByOrganizationIdAndPublicId(organizationId, chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found: " + chatId));

        if(!chat.hasParticipant(currentUserId)) {
            throw new ForbiddenException("Access denied to chat: " + chatId);
        }

        return directChatMessageRepository.findByChatOrderByCreatedAtDesc(chat, pageable)
                .map(DirectChatMessageResponse::mapDirectChatMessageToResponse);
    }

    @Override
    public DirectChatMessageResponse sendMessage(String userId, DirectChatMessageRequest request) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        User sender = chatAccessGuard.getUserOrThrow(userId);

        DirectChat chat = directChatRepository.findByOrganizationIdAndPublicId(organizationId, request.getChatId())
                .orElseThrow(() -> new NotFoundException("Chat not found: " + request.getChatId()));

        if (!chat.hasParticipant(userId)) {
            throw new ForbiddenException("Access denied to chat: " + request.getChatId());
        }

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

        DirectChatMessage message = DirectChatMessage.builder()
                .chat(chat)
                .sender(sender)
                .message(request.getMessage())
                .containsMedia(containsMedia)
                .mediaFile(mediaFile)
                .isRead(false)
                .build();

        message = directChatMessageRepository.save(message);
        return DirectChatMessageResponse.mapDirectChatMessageToResponse(message);
    }
}
