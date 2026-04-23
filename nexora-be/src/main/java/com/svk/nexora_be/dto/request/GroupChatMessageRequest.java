package com.svk.nexora_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatMessageRequest {
    private String groupId;
    private String message;
    private String token;
}
