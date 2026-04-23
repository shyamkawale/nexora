package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.GroupChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatMessageResponse {
    private String publicId;
    private String message;
    private UserResponse sender;
    private GroupResponse group;
    private Boolean containsMedia;
    private String messageStatus;
    private LocalDateTime createdAt;

    public static GroupChatMessageResponse fromGroupChatMessage(GroupChatMessage msg) {
        return GroupChatMessageResponse.builder()
                .publicId(msg.getPublicId())
                .message(msg.getMessage())
                .sender(UserResponse.fromUser(msg.getSender()))
                .group(GroupResponse.fromGroup(msg.getGroup()))
                .containsMedia(msg.getContainsMedia())
                .messageStatus(msg.getMessageStatus().name())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
