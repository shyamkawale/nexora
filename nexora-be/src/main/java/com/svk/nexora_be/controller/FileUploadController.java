package com.svk.nexora_be.controller;

import com.svk.nexora_be.service.S3UploadService;
import com.svk.nexora_be.service.S3UploadService.PresignedUrlRequest;
import com.svk.nexora_be.service.S3UploadService.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
public class FileUploadController {
    private final S3UploadService s3UploadService;

    /**
     * Generate presigned URL for file upload to S3
     * 
     * @param request Contains fileName, mimeType, fileSizeBytes
     * @return Presigned URL response with upload details
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(@RequestBody PresignedUrlRequest request) {
        try {
            PresignedUrlResponse response = s3UploadService.generatePresignedUrl(
                request.getFileName(),
                request.getMimeType(),
                request.getFileSizeBytes()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Get presigned URL for file download or viewing
     * 
     * @param fileKey S3 file key (passed as query parameter)
     * @param download if true, forces download with attachment disposition; if false or omitted, allows inline viewing
     * @return JSON response with presigned URL
     */
    @GetMapping("/download-url")
    public ResponseEntity<?> getDownloadUrl(@RequestParam String fileKey, @RequestParam(required = false, defaultValue = "false") boolean download) {
        try {
            // Generate presigned URL with download mode
            String presignedUrl = s3UploadService.generatePresignedDownloadUrl(fileKey, download);

            // Return JSON response
            return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
                put("presignedUrl", presignedUrl);
                put("fileName", fileKey.substring(fileKey.lastIndexOf('/') + 1));
            }});
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new java.util.HashMap<String, String>() {{
                put("error", "Failed to generate URL: " + e.getMessage());
            }});
        }
    }
}

