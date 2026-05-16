package com.svk.nexora_be.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastGroupMessage(String groupChatId, Object message) {
        String topicPath = "/topic/group-messages/" + groupChatId;
        messagingTemplate.convertAndSend(topicPath, message);
    }

    public void broadcastDirectMessage(String chatId, Object message) {
        String topicPath = "/topic/messages/" + chatId;
        messagingTemplate.convertAndSend(topicPath, message);
    }
}
