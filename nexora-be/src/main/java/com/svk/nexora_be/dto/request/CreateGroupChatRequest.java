package com.svk.nexora_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupChatRequest {
    private String groupName;
    private String description;
    private List<String> memberPublicIds;
}
