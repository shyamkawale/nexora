package com.svk.nexora_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessageResponse {
    private String publicId;
    private String message;
    private Long createdAt;
    private UserResponse sender;
    private String groupChatId;
}
