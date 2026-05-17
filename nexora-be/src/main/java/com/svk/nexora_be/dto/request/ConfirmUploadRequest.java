package com.svk.nexora_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmUploadRequest {
    /**
     * Public id of the {@code MediaFile} record returned alongside the presigned upload URL.
     */
    private String mediaFilePublicId;
}
