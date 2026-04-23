package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.DirectMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectMessageResponse {
    private String publicId;
    private String message;
    private UserResponse sender;
    private Boolean isRead;
    private Boolean containsMedia;
    private LocalDateTime createdAt;

    public static DirectMessageResponse fromDirectMessage(DirectMessage msg) {
        return DirectMessageResponse.builder()
                .publicId(msg.getPublicId())
                .message(msg.getMessage())
                .sender(UserResponse.fromUser(msg.getSender()))
                .isRead(msg.getIsRead())
                .containsMedia(msg.getContainsMedia())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
