package com.svk.nexora_be.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svk.nexora_be.entity.GroupChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Boolean isActive;

    public static GroupChatResponse mapGroupChatToResponse(GroupChat groupChat) {
        List<UserResponse> members = groupChat.getMembers().stream()
                .filter(m -> m.getIsActive())
                .map(m -> UserResponse.builder()
                        .publicId(m.getUser().getPublicId())
                        .username(m.getUser().getUsername())
                        .email(m.getUser().getEmail())
                        .profilePicture(m.getUser().getProfilePicture())
                        .bio(m.getUser().getBio())
                        .build())
                .collect(Collectors.toList());

        return GroupChatResponse.builder()
                .publicId(groupChat.getPublicId())
                .groupName(groupChat.getGroupName())
                .description(groupChat.getDescription())
                .createdBy(UserResponse.builder()
                        .publicId(groupChat.getCreatedBy().getPublicId())
                        .username(groupChat.getCreatedBy().getUsername())
                        .email(groupChat.getCreatedBy().getEmail())
                        .profilePicture(groupChat.getCreatedBy().getProfilePicture())
                        .bio(groupChat.getCreatedBy().getBio())
                        .build())
                .members(members)
                .createdAt(groupChat.getCreatedAt())
                .isActive(groupChat.getIsActive())
                .build();
    }
}
