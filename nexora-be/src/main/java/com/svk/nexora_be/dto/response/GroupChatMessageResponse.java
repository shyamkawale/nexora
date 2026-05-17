package com.svk.nexora_be.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private UserResponse sender;
    private String groupChatId;
    private Boolean containsMedia;
    private MediaFileResponse mediaFile;

    public static GroupChatMessageResponse mapGroupChatMessageToResponse(GroupChatMessage message) {
        return GroupChatMessageResponse.builder()
                .publicId(message.getPublicId())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .sender(UserResponse.builder()
                        .publicId(message.getSender().getPublicId())
                        .username(message.getSender().getUsername())
                        .email(message.getSender().getEmail())
                        .profilePicture(message.getSender().getProfilePicture())
                        .bio(message.getSender().getBio())
                        .build())
                .groupChatId(message.getGroupChat().getPublicId())
                .containsMedia(message.getContainsMedia())
                .mediaFile(MediaFileResponse.fromEntity(message.getMediaFile()))
                .build();
    }
}
