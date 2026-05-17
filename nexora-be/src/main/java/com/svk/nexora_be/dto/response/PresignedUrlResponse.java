package com.svk.nexora_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignedUrlResponse {
    private String fileKey;
    private String presignedUrl;
    private long expirationMinutes;
    private String fileUrl;
}
