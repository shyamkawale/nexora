package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String publicId;
    private String email;
    private String username;
    private String profilePicture;
    private String bio;
    private Boolean isActive;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .build();
    }
}
