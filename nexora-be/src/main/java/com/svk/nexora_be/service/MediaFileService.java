package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.response.MediaFileResponse;
import com.svk.nexora_be.entity.MediaFile;

/**
 * Tracks uploaded media files in the database so they can be referenced from chat messages
 * (and other entities), audited per user, and cleaned up when abandoned.
 *
 * <p>Lifecycle:
 * <ol>
 *     <li>{@link #registerPendingUpload} – called when a presigned upload URL is issued.</li>
 *     <li>{@link #confirmUpload} – called by the client (or a verifier) once the upload
 *     to S3 has succeeded.</li>
 *     <li>{@link #getActiveByPublicId} – used by chat services to attach a confirmed file
 *     to a message.</li>
 * </ol>
 */
public interface MediaFileService {

    /**
     * Persist a new {@link MediaFile} row in {@code PENDING} state for an issued
     * presigned upload URL. The returned entity's {@code publicId} is what the client
     * later passes back when sending a chat message.
     */
    MediaFile registerPendingUpload(String userPublicId,
                                    String fileName,
                                    String mimeType,
                                    long fileSizeBytes,
                                    String fileKey);

    /**
     * Mark a previously-registered upload as confirmed. The caller (the upload owner)
     * must match the original uploader; otherwise a {@code ForbiddenException} is thrown.
     */
    MediaFileResponse confirmUpload(String userPublicId, String mediaFilePublicId);

    /**
     * Resolve a confirmed media file by its public id. Throws if not found or if the
     * upload is not yet in {@code UPLOADED} state.
     */
    MediaFile getActiveByPublicId(String mediaFilePublicId);

    /**
     * Read-only fetch of a media file (any status), used by callers that want to expose
     * media metadata in a response.
     */
    MediaFile getByPublicId(String mediaFilePublicId);

    MediaFile getByFilePath(String filePath);
}
