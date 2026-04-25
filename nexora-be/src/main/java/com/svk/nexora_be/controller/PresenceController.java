package com.svk.nexora_be.controller;

import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PresenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.svk.nexora_be.security.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/presence")
@AllArgsConstructor
@Slf4j
public class PresenceController {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Get all online users
     */
    @GetMapping("/online-users")
    public ResponseEntity<Map<String, Object>> getOnlineUsers() {
        try {
            List<String> onlineUsers = presenceService.getAllOnlineUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("onlineUsers", onlineUsers);
            response.put("count", onlineUsers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch online users");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Check if specific user is online
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkUserOnline(String userId) {
        try {
            boolean isOnline = presenceService.isUserOnline(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("isOnline", isOnline);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check user status");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * WebSocket endpoint for presence updates
     * Clients can send updates via /app/presence/update
     */
    @MessageMapping("/presence/update")
    @SendTo("/topic/presence/updates")
    public Map<String, Object> handlePresenceUpdate(Map<String, Object> message) {
        String userId = jwtUtil.getCurrentUserId();

        log.info("📍 Presence update received from: {}", userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", message.get("message"));

        return response;
    }

    /**
     * Mark user as offline when they close browser without logout
     * Called via navigator.sendBeacon() from beforeunload handler
     */
    @PostMapping("/offline")
    public ResponseEntity<Map<String, String>> markOfflineOnUnload() {
        try {
            String userId = jwtUtil.getCurrentUserId();
            if (userId != null) {
                log.info("📴 User {} closed browser - marking offline", userId);
                // Fetch user and mark as offline
                userRepository.findByPublicId(userId)
                        .ifPresent(presenceService::markUserOffline);
            }

            Map<String, String> response = new HashMap<>();
            response.put("status", "offline");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("⚠️ Error marking user offline on unload: {}", e.getMessage());
            // Still return 200 to avoid delays on page unload
            Map<String, String> response = new HashMap<>();
            response.put("status", "processed");
            return ResponseEntity.ok(response);
        }
    }
}
