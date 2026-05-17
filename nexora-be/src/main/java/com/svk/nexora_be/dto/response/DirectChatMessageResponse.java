package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.DirectChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatMessageResponse {
    private String publicId;
    private String message;
    private UserResponse sender;
    private Boolean isRead;
    private Boolean containsMedia;
    private MediaFileResponse mediaFile;
    private LocalDateTime createdAt;

    public static DirectChatMessageResponse mapDirectChatMessageToResponse(DirectChatMessage msg) {
        return DirectChatMessageResponse.builder()
                .publicId(msg.getPublicId())
                .message(msg.getMessage())
                .sender(UserResponse.fromUser(msg.getSender()))
                .isRead(msg.getIsRead())
                .containsMedia(msg.getContainsMedia())
                .mediaFile(MediaFileResponse.fromEntity(msg.getMediaFile()))
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
