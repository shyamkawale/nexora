package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.OnlineUsersResponse;
import com.svk.nexora_be.dto.response.PresenceUpdateMessage;
import com.svk.nexora_be.dto.response.PresenceUpdateResponse;
import com.svk.nexora_be.dto.response.UserOnlineStatusResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PresenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

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
    public ResponseEntity<ApiResponse<OnlineUsersResponse>> getOnlineUsers() {
        List<String> onlineUsers = presenceService.getAllOnlineUsers();
        OnlineUsersResponse dto = OnlineUsersResponse.builder()
                .onlineUsers(onlineUsers)
                .count(onlineUsers.size())
                .build();
        return ResponseEntity.ok(ApiResponse.success(dto, "Online users fetched"));
    }

    /**
     * Check if specific user is online
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<ApiResponse<UserOnlineStatusResponse>> checkUserOnline(String userId) {
        boolean isOnline = presenceService.isUserOnline(userId);
        UserOnlineStatusResponse dto = UserOnlineStatusResponse.builder()
                .userId(userId)
                .isOnline(isOnline)
                .build();
        return ResponseEntity.ok(ApiResponse.success(dto, "User status fetched"));
    }

    /**
     * WebSocket endpoint for presence updates
     * Clients can send updates via /app/presence/update
     */
    @MessageMapping("/presence/update")
    @SendTo("/topic/presence/updates")
    public PresenceUpdateMessage handlePresenceUpdate(java.util.Map<String, Object> message) {
        String userId = jwtUtil.getCurrentUserId();

        log.info("📍 Presence update received from: {}", userId);

        PresenceUpdateMessage response = PresenceUpdateMessage.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .message(message.getOrDefault("message", "").toString())
                .build();

        return response;
    }

    /**
     * Mark user as offline when they close browser without logout
     * Called via navigator.sendBeacon() from beforeunload handler
     */
    @PostMapping("/offline")
    public ResponseEntity<ApiResponse<PresenceUpdateResponse>> markOfflineOnUnload() {
        try {
            String userId = jwtUtil.getCurrentUserId();
            if (userId != null) {
                log.info("📴 User {} closed browser - marking offline", userId);
                // Fetch user and mark as offline
                userRepository.findByPublicId(userId)
                        .ifPresent(presenceService::markUserOffline);
            }

            PresenceUpdateResponse dto = PresenceUpdateResponse.builder()
                    .userId(jwtUtil.getCurrentUserId())
                    .status("offline")
                    .message("Marked offline")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(dto, "Processed"));
        } catch (Exception e) {
            log.warn("⚠️ Error marking user offline on unload: {}", e.getMessage());
            // Still return 200 to avoid delays on page unload
            PresenceUpdateResponse dto = PresenceUpdateResponse.builder()
                    .userId(null)
                    .status("processed")
                    .message("Error during processing")
                    .build();
            return ResponseEntity.ok(ApiResponse.success(dto, "Processed with warnings"));
        }
    }
}
