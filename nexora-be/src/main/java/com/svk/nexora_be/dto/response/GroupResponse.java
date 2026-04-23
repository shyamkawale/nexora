package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {
    private String publicId;
    private String name;
    private String description;
    private String profilePicture;
    private UserResponse creator;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static GroupResponse fromGroup(Group group) {
        return GroupResponse.builder()
                .publicId(group.getPublicId())
                .name(group.getName())
                .description(group.getDescription())
                .profilePicture(group.getProfilePicture())
                .creator(UserResponse.fromUser(group.getCreator()))
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
