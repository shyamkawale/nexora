package com.svk.nexora_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatResponse {
    private String publicId;
    private String groupName;
    private String description;
    private UserResponse createdBy;
    private List<UserResponse> members;
    private Long createdAt;
    private Boolean isActive;
}
