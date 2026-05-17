package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.ConfirmUploadRequest;
import com.svk.nexora_be.dto.request.PresignedUrlRequest;
import com.svk.nexora_be.dto.response.MediaFileResponse;
import com.svk.nexora_be.dto.response.PresignedUrlForDownloadResponse;
import com.svk.nexora_be.dto.response.PresignedUrlForUploadResponse;
import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.security.JwtUtil;
import com.svk.nexora_be.service.MediaFileService;
import com.svk.nexora_be.service.S3UploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
@Slf4j
public class FileUploadController {
    private final S3UploadService s3UploadService;
    private final MediaFileService mediaFileService;
    private final JwtUtil jwtUtil;

    /**
     * Generate a presigned URL for uploading a file to S3 and persist a {@code MediaFile}
     * row in PENDING state. The client must subsequently call
     * {@link #confirmUpload(ConfirmUploadRequest)} once the PUT to S3 has succeeded.
     */
    @PostMapping("/presigned-upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlForUploadResponse>> generatePresignedUrl(
            @RequestBody PresignedUrlRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        try {
            PresignedUrlForUploadResponse response = s3UploadService.generatePresignedUrl(
                    request.getFileName(),
                    request.getMimeType(),
                    request.getFileSizeBytes()
            );

            MediaFile pending = mediaFileService.registerPendingUpload(
                    currentUserId,
                    request.getFileName(),
                    request.getMimeType(),
                    request.getFileSizeBytes(),
                    response.getFileKey()
            );
            response.setMediaFilePublicId(pending.getPublicId());

            return ResponseEntity.ok(ApiResponse.success(response, "Presigned URL generated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to generate presigned upload URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Failed to generate presigned URL"));
        }
    }

    /**
     * Mark a previously-registered upload as confirmed (i.e. the bytes have actually
     * landed in S3). Only the original uploader may confirm. The returned
     * {@code mediaFilePublicId} is what the client should attach to a chat message.
     */
    @PostMapping("/confirm-upload")
    public ResponseEntity<ApiResponse<MediaFileResponse>> confirmUpload(
            @RequestBody ConfirmUploadRequest request) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        if (request == null || request.getMediaFilePublicId() == null
                || request.getMediaFilePublicId().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    HttpStatus.BAD_REQUEST.value(), "mediaFilePublicId is required"));
        }

        MediaFileResponse response = mediaFileService.confirmUpload(
                currentUserId, request.getMediaFilePublicId());
        return ResponseEntity.ok(ApiResponse.success(response, "Upload confirmed"));
    }

    /**
     * Get presigned URL for file download or viewing
     */
    @GetMapping("/presigned-download-url")
    public ResponseEntity<ApiResponse<PresignedUrlForDownloadResponse>> getDownloadUrl(
            @RequestParam String fileKey,
            @RequestParam(required = false, defaultValue = "false") boolean download) {
        try {
            PresignedUrlForDownloadResponse response =
                    s3UploadService.generatePresignedDownloadUrl(fileKey, download);
            return ResponseEntity.ok(ApiResponse.success(response, "Download URL generated"));
        } catch (Exception e) {
            log.error("Failed to generate download URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Failed to generate URL: " + e.getMessage()));
        }
    }
}
