package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.response.MediaFileResponse;
import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.MediaFileStatus;
import com.svk.nexora_be.exception.ForbiddenException;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.MediaFileRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFileRepository mediaFileRepository;
    private final UserRepository userRepository;

    @Override
    public MediaFile registerPendingUpload(String userPublicId,
                                           String fileName,
                                           String mimeType,
                                           long fileSizeBytes,
                                           String fileKey) {
        User uploader = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userPublicId));

        MediaFile mediaFile = MediaFile.builder()
                .fileName(fileName)
                .fileType(mimeType)
                .fileSize(fileSizeBytes)
                .filePath(fileKey)
                .uploadedBy(uploader)
                .status(MediaFileStatus.PENDING)
                .build();

        mediaFile = mediaFileRepository.save(mediaFile);
        log.info("📎 Registered pending media file id={} key={} owner={}",
                mediaFile.getPublicId(), fileKey, userPublicId);
        return mediaFile;
    }

    @Override
    public MediaFileResponse confirmUpload(String userPublicId, String mediaFilePublicId) {
        MediaFile mediaFile = mediaFileRepository.findByPublicId(mediaFilePublicId)
                .orElseThrow(() -> new NotFoundException(
                        "Media file not found: " + mediaFilePublicId));

        if (mediaFile.getUploadedBy() == null
                || !mediaFile.getUploadedBy().getPublicId().equals(userPublicId)) {
            throw new ForbiddenException(
                    "Only the original uploader can confirm this media file");
        }

        // Idempotent: re-confirming an already-uploaded file is a no-op (just return it).
        if (mediaFile.getStatus() != MediaFileStatus.UPLOADED) {
            mediaFile.setStatus(MediaFileStatus.UPLOADED);
            mediaFile.setConfirmedAt(LocalDateTime.now());
            mediaFile = mediaFileRepository.save(mediaFile);
            log.info("✅ Confirmed media upload id={}", mediaFilePublicId);
        }

        return MediaFileResponse.fromEntity(mediaFile);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaFile getActiveByPublicId(String mediaFilePublicId) {
        MediaFile mediaFile = getByPublicId(mediaFilePublicId);
        if (mediaFile.getStatus() != MediaFileStatus.UPLOADED) {
            throw new ForbiddenException(
                    "Media file " + mediaFilePublicId
                            + " is not in UPLOADED state (current: " + mediaFile.getStatus() + ")");
        }
        return mediaFile;
    }

    @Override
    @Transactional(readOnly = true)
    public MediaFile getByPublicId(String mediaFilePublicId) {
        return mediaFileRepository.findByPublicId(mediaFilePublicId)
                .orElseThrow(() -> new NotFoundException(
                        "Media file not found: " + mediaFilePublicId));
    }
}
