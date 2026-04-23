package com.svk.nexora_be.service;

import com.svk.nexora_be.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "aws.s3.access-key")
@RequiredArgsConstructor
@Slf4j
public class S3UploadService {
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    /**
     * Generate presigned URL for file upload
     */
    public PresignedUrlResponse generatePresignedUrl(String fileName, String mimeType, long fileSizeBytes) {
        try {
            // Validate file size
            long maxSizeBytes = s3Properties.getMaxFileSizeMb() * 1024 * 1024;
            if (fileSizeBytes > maxSizeBytes) {
                throw new IllegalArgumentException(
                    String.format("File size %d MB exceeds maximum allowed size of %d MB", 
                        fileSizeBytes / (1024 * 1024), 
                        s3Properties.getMaxFileSizeMb()
                    )
                );
            }

            // Generate unique file key with timestamp to avoid conflicts
            String fileKey = generateFileKey(fileName);
            
            log.info("🔑 Generating presigned URL for file: {} (key: {})", fileName, fileKey);

            // Create S3Presigner
            S3Presigner presigner = S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(s3Properties.getRegion()))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                    software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                        s3Properties.getAccessKey(),
                        s3Properties.getSecretKey()
                    )
                ))
                .build();

            // Build presigned URL
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileKey)
                .contentType(mimeType)
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.getPresignedUrlExpirationMinutes()))
                .putObjectRequest(putRequest)
                .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.info("✅ Presigned URL generated successfully");
            presigner.close();

            return new PresignedUrlResponse(
                fileKey,
                presignedUrl,
                s3Properties.getPresignedUrlExpirationMinutes(),
                getFileUrl(fileKey)
            );
        } catch (Exception e) {
            log.error("❌ Error generating presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage());
        }
    }

    /**
     * Generate presigned URL for file download/viewing
     * @param fileKey the S3 file key
     * @param forceDownload if true, adds attachment disposition to force download; if false, allows inline viewing
     */
    public String generatePresignedDownloadUrl(String fileKey, boolean forceDownload) {
        try {
            log.info("🔑 Generating presigned download URL for file key: {}, forceDownload: {}", fileKey, forceDownload);

            // Create S3Presigner
            S3Presigner presigner = S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(s3Properties.getRegion()))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                    software.amazon.awssdk.auth.credentials.AwsBasicCredentials.create(
                        s3Properties.getAccessKey(),
                        s3Properties.getSecretKey()
                    )
                ))
                .build();

            String fileName = fileKey.substring(fileKey.lastIndexOf('/') + 1);

            // Build GetObject request
            GetObjectRequest.Builder getObjectRequestBuilder = GetObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(fileKey);

            // Add attachment disposition only if forceDownload is true
            if (forceDownload) {
                getObjectRequestBuilder.responseContentDisposition("attachment; filename=\"" + fileName + "\"");
            }
            
            GetObjectRequest getObjectRequest = getObjectRequestBuilder.build();

            // Create presigned request with 24-hour expiration for downloads
            GetObjectPresignRequest getPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getPresignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.info("✅ Presigned download URL generated successfully");
            presigner.close();

            return presignedUrl;
        } catch (Exception e) {
            log.error("❌ Error generating presigned download URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate download URL: " + e.getMessage());
        }
    }

    /**
     * Generate presigned URL for file download (with attachment disposition)
     */
    public String generatePresignedDownloadUrl(String fileKey) {
        return generatePresignedDownloadUrl(fileKey, true);
    }
    public String getFileUrl(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            s3Properties.getBucketName(),
            s3Properties.getRegion(),
            fileKey
        );
    }

    /**
     * Generate a unique file key for S3 storage
     */
    private String generateFileKey(String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        // Keep original file extension
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        return String.format("collab-gen/chat-files/%s_%s%s", timestamp, uuid, fileExtension);
    }

    /**
     * Response DTO for presigned URL
     */
    public static class PresignedUrlResponse {
        public String fileKey;
        public String presignedUrl;
        public long expirationMinutes;
        public String fileUrl;

        public PresignedUrlResponse(String fileKey, String presignedUrl, long expirationMinutes, String fileUrl) {
            this.fileKey = fileKey;
            this.presignedUrl = presignedUrl;
            this.expirationMinutes = expirationMinutes;
            this.fileUrl = fileUrl;
        }

        // Getters for JSON serialization
        public String getFileKey() { return fileKey; }
        public String getPresignedUrl() { return presignedUrl; }
        public long getExpirationMinutes() { return expirationMinutes; }
        public String getFileUrl() { return fileUrl; }
    }

    /**
     * Request DTO for presigned URL generation
     */
    public static class PresignedUrlRequest {
        public String fileName;
        public String mimeType;
        public long fileSizeBytes;

        // Getters and setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        
        public long getFileSizeBytes() { return fileSizeBytes; }
        public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    }
}
