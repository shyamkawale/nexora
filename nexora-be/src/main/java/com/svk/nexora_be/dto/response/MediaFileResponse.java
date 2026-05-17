package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.enums.MediaFileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFileResponse {
    private String publicId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileKey;
    private MediaFileStatus status;
    private String uploadedByPublicId;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    /**
     * Optional short-lived presigned URL for inline viewing/downloading. Populated by
     * services when they want to hand the client a ready-to-fetch link; null otherwise.
     */
    private String presignedDownloadUrl;

    public static MediaFileResponse fromEntity(MediaFile mediaFile) {
        if (mediaFile == null) {
            return null;
        }
        return MediaFileResponse.builder()
                .publicId(mediaFile.getPublicId())
                .fileName(mediaFile.getFileName())
                .fileType(mediaFile.getFileType())
                .fileSize(mediaFile.getFileSize())
                .fileKey(mediaFile.getFilePath())
                .status(mediaFile.getStatus())
                .uploadedByPublicId(mediaFile.getUploadedBy() != null
                        ? mediaFile.getUploadedBy().getPublicId()
                        : null)
                .createdAt(mediaFile.getCreatedAt())
                .confirmedAt(mediaFile.getConfirmedAt())
                .build();
    }

    public static MediaFileResponse fromEntity(MediaFile mediaFile, String presignedDownloadUrl) {
        MediaFileResponse response = fromEntity(mediaFile);
        if (response != null) {
            response.setPresignedDownloadUrl(presignedDownloadUrl);
        }
        return response;
    }
}
