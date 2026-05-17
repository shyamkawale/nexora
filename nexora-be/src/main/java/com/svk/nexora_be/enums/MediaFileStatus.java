package com.svk.nexora_be.enums;

/**
 * Lifecycle status of a {@link com.svk.nexora_be.entity.MediaFile} record.
 *
 * <ul>
 *     <li>{@link #PENDING} – row created when the presigned upload URL was issued, but the
 *     client has not yet confirmed that the bytes were uploaded to S3.</li>
 *     <li>{@link #UPLOADED} – client confirmed the upload (and/or the server verified the
 *     object exists in S3). Safe to attach to chat messages or other entities.</li>
 *     <li>{@link #FAILED} – upload was abandoned or could not be verified. Such rows are
 *     candidates for cleanup jobs.</li>
 * </ul>
 */
public enum MediaFileStatus {
    PENDING,
    UPLOADED,
    FAILED
}
