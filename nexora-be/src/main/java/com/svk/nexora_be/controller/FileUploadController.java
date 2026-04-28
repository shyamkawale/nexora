package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.DownloadUrlResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.S3UploadService;
import com.svk.nexora_be.service.S3UploadService.PresignedUrlRequest;
import com.svk.nexora_be.service.S3UploadService.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
public class FileUploadController {
    private final S3UploadService s3UploadService;

    /**
     * Generate presigned URL for file upload
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(@RequestBody PresignedUrlRequest request) {
        try {
            PresignedUrlResponse response = s3UploadService.generatePresignedUrl(
                request.getFileName(),
                request.getMimeType(),
                request.getFileSizeBytes()
            );
            return ResponseEntity.ok(ApiResponse.success(response, "Presigned URL generated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to generate presigned URL"));
        }
    }

    /**
     * Get presigned URL for file download or viewing
     */
    @GetMapping("/download-url")
    public ResponseEntity<ApiResponse<DownloadUrlResponse>> getDownloadUrl(@RequestParam String fileKey, @RequestParam(required = false, defaultValue = "false") boolean download) {
        try {
            String presignedUrl = s3UploadService.generatePresignedDownloadUrl(fileKey, download);
            DownloadUrlResponse dto = DownloadUrlResponse.builder()
                    .presignedUrl(presignedUrl)
                    .fileName(fileKey.substring(fileKey.lastIndexOf('/') + 1))
                    .build();
            return ResponseEntity.ok(ApiResponse.success(dto, "Download URL generated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to generate URL: " + e.getMessage()));
        }
    }
}

