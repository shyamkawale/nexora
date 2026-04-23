package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.DirectMessageRequest;
import com.svk.nexora_be.dto.response.DirectMessageResponse;
import com.svk.nexora_be.entity.DirectMessage;
import com.svk.nexora_be.entity.DirectMessageChat;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.DirectMessageChatRepository;
import com.svk.nexora_be.repository.DirectMessageRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepository directMessageRepository;
    private final DirectMessageChatRepository directMessageChatRepository;
    private final UserRepository userRepository;

    @Override
    public DirectMessageChat getOrCreateChat(String userId, String otherUserId) {
        User user = userRepository.findByPublicId(userId).orElse(null);
        User otherUser = userRepository.findByPublicId(otherUserId).orElse(null);

        if (user == null || otherUser == null) {
            return null;
        }

        return directMessageChatRepository.findChatBetweenUsers(user, otherUser)
                .orElseGet(() -> {
                    DirectMessageChat chat = DirectMessageChat.builder()
                            .user1(user)
                            .user2(otherUser)
                            .build();
                    return directMessageChatRepository.save(chat);
                });
    }

    @Override
    public DirectMessageResponse sendMessage(String userId, DirectMessageRequest request) {
        User sender = userRepository.findByPublicId(userId).orElse(null);

        if (sender == null) {
            return null;
        }

        DirectMessageChat chat = directMessageChatRepository.findByPublicId(request.getChatId())
                .orElse(null);

        if (chat == null) {
            return null;
        }

        DirectMessage message = DirectMessage.builder()
                .chat(chat)
                .sender(sender)
                .message(request.getMessage())
                .containsMedia(false)
                .isRead(false)
                .build();

        message = directMessageRepository.save(message);
        return DirectMessageResponse.fromDirectMessage(message);
    }

    @Override
    public Page<DirectMessageResponse> getMessages(String chatId, Pageable pageable) {
        DirectMessageChat chat = directMessageChatRepository.findByPublicId(chatId)
                .orElse(null);

        if (chat == null) {
            return Page.empty(pageable);
        }

        return directMessageRepository.findByChatOrderByCreatedAtDesc(chat, pageable)
                .map(DirectMessageResponse::fromDirectMessage);
    }

    @Override
    public List<DirectMessageChat> getUserChats(String userId) {
        User user = userRepository.findByPublicId(userId).orElse(null);
        if (user == null) {
            return List.of();
        }
        return directMessageChatRepository.findAllChatsForUser(user);
    }
}
