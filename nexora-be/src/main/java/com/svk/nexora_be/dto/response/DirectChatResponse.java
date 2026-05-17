package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.DirectChat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatResponse {
    private String publicId;
    private UserResponse user1;
    private UserResponse user2;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public static DirectChatResponse mapDirectChatToResponse(DirectChat chat) {
        return DirectChatResponse.builder()
                .publicId(chat.getPublicId())
                .user1(UserResponse.fromUser(chat.getUser1()))
                .user2(UserResponse.fromUser(chat.getUser2()))
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
