package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.MediaFile;
import com.svk.nexora_be.enums.MediaFileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    Optional<MediaFile> findByPublicId(String publicId);

    Optional<MediaFile> findByOrganizationIdAndPublicId(Long organizationId, String publicId);

    Optional<MediaFile> findByFilePath(String filePath);

    Optional<MediaFile> findByOrganizationIdAndFilePath(Long organizationId, String filePath);

    Page<MediaFile> findByUploadedByPublicIdOrderByCreatedAtDesc(String userPublicId, Pageable pageable);

    Page<MediaFile> findByOrganizationIdAndUploadedByPublicIdOrderByCreatedAtDesc(
            Long organizationId,
            String userPublicId,
            Pageable pageable
    );

    /**
     * Find media files in PENDING state created before the given cutoff. Useful for a
     * scheduled cleanup job that purges abandoned uploads.
     */
    List<MediaFile> findByStatusAndCreatedAtBefore(MediaFileStatus status, LocalDateTime cutoff);
}
