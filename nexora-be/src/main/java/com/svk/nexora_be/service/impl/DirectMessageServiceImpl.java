package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageChatResponse;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.DirectMessage;
import com.svk.nexora_be.entity.DirectMessageChat;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.DirectMessageChatRepository;
import com.svk.nexora_be.repository.DirectMessageRepository;
import com.svk.nexora_be.service.ChatAccessGuard;
import com.svk.nexora_be.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final DirectMessageChatRepository directMessageChatRepository;
    private final ChatAccessGuard chatAccessGuard;

    @Override
    public DirectMessageChatResponse getOrCreateChat(String userId, String otherUserId) {
        User user = chatAccessGuard.getUserOrThrow(userId);
        User otherUser = chatAccessGuard.getUserOrThrow(otherUserId);

        DirectMessageChat chat = directMessageChatRepository.findChatBetweenUsers(user, otherUser)
                .orElseGet(() -> {
                    DirectMessageChat newChat = DirectMessageChat.builder()
                            .user1(user)
                            .user2(otherUser)
                            .build();
                    return directMessageChatRepository.save(newChat);
                });
        
        return mapChatToResponse(chat);
    }

    @Override
    public DirectMessageResponse sendMessage(String userId, DirectMessageRequest request) {
        User sender = chatAccessGuard.getUserOrThrow(userId);

        DirectMessageChat chat = directMessageChatRepository.findByPublicId(request.getChatId())
                .orElseThrow(() -> new NotFoundException("Chat not found: " + request.getChatId()));

        DirectMessage message = DirectMessage.builder()
                .chat(chat)
                .sender(sender)
                .message(request.getMessage())
                .containsMedia(request.isContainsMedia())
                .isRead(false)
                .build();

        message = directMessageRepository.save(message);
        return DirectMessageResponse.fromDirectMessage(message);
    }

    @Override
    public Page<DirectMessageResponse> getMessages(String chatId, Pageable pageable) {
        DirectMessageChat chat = directMessageChatRepository.findByPublicId(chatId)
                .orElseThrow(() -> new NotFoundException("Chat not found: " + chatId));

        return directMessageRepository.findByChatOrderByCreatedAtDesc(chat, pageable)
                .map(DirectMessageResponse::fromDirectMessage);
    }

    @Override
    public List<DirectMessageChatResponse> getUserChats(String userId) {
        User user = chatAccessGuard.getUserOrThrow(userId);
        return directMessageChatRepository.findAllChatsForUser(user)
                .stream()
                .map(this::mapChatToResponse)
                .toList();
    }

    private DirectMessageChatResponse mapChatToResponse(DirectMessageChat chat) {
        return DirectMessageChatResponse.builder()
                .publicId(chat.getPublicId())
                .user1(UserResponse.fromUser(chat.getUser1()))
                .user2(UserResponse.fromUser(chat.getUser2()))
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
