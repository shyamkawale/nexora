package com.svk.nexora_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectChatMessageRequest {
    private String chatId;
    private String message;
    private boolean containsMedia;
    /**
     * Optional public id of a confirmed {@code MediaFile} to attach to this message.
     * When set, the server resolves it, verifies it is in {@code UPLOADED} state, and
     * links it to the persisted message.
     */
    private String mediaFilePublicId;
}
