package com.svk.nexora_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignedUrlForUploadResponse {
    private String fileKey;
    private String presignedUrl;
    private long expirationMinutes;
    private String fileUrl;
    /**
     * Public id of the {@code MediaFile} row that tracks this upload. Clients must
     * (a) PUT the bytes to {@code presignedUrl}, and (b) call the confirm-upload
     * endpoint with this id once the PUT succeeds.
     */
    private String mediaFilePublicId;
}
