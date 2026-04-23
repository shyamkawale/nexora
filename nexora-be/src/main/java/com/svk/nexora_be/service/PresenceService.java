package com.svk.nexora_be.service;

import com.svk.nexora_be.entity.User;

public interface PresenceService {
    /**
     * Mark user as online
     */
    void markUserOnline(User user);

    /**
     * Mark user as offline
     */
    void markUserOffline(User user);

    /**
     * Check if user is online
     */
    boolean isUserOnline(String userId);

    /**
     * Get all online users
     */
    java.util.List<String> getAllOnlineUsers();

    /**
     * Broadcast presence update to all subscribers
     */
    void broadcastPresenceUpdate(String userId, boolean isOnline);
}
