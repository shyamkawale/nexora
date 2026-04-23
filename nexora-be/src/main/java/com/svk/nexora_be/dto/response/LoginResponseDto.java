package com.svk.nexora_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String userId;
    private String username;
    private String email;
    private String profilePicture;
    private String role;
}
