package com.svk.nexora_be.service;

import com.svk.nexora_be.tenant.OrganizationContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastGroupMessage(String groupChatId, Object message) {
        String topicPath = "/topic/org/" + OrganizationContextHolder.requireOrganizationPublicId()
                + "/group-messages/" + groupChatId;
        messagingTemplate.convertAndSend(topicPath, message);
    }

    public void broadcastDirectMessage(String chatId, Object message) {
        String topicPath = "/topic/org/" + OrganizationContextHolder.requireOrganizationPublicId()
                + "/messages/" + chatId;
        messagingTemplate.convertAndSend(topicPath, message);
    }
}
