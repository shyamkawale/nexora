package com.svk.nexora_be.entity;

import com.svk.nexora_be.enums.MediaFileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String publicId;

    @Column(nullable = false, length = 500)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, unique = true, length = 512)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    private MediaFileStatus status = MediaFileStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime confirmedAt;

    @PrePersist
    protected void onCreate() {
        if (publicId == null) {
            publicId = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = MediaFileStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }
}
