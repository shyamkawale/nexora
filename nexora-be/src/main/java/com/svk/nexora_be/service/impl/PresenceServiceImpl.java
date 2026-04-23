package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceServiceImpl implements PresenceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String ONLINE_USERS_KEY = "presence:online_users";
    private static final String USER_PRESENCE_KEY_PREFIX = "presence:user:";
    private static final long PRESENCE_TIMEOUT_SECONDS = 300; // 5 minutes

    @Override
    @Transactional
    public void markUserOnline(User user) {
        try {
            // Update user entity
            user.setIsOnline(true);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);

            // Store in Redis with expiry
            String presenceKey = USER_PRESENCE_KEY_PREFIX + user.getPublicId();
            redisTemplate.opsForValue().set(presenceKey, "online", PRESENCE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // Add to online users set
            redisTemplate.opsForSet().add(ONLINE_USERS_KEY, user.getPublicId());

            log.info("✅ User marked online: {}", user.getPublicId());

            // Broadcast to all subscribers
            broadcastPresenceUpdate(user.getPublicId(), true);

        } catch (Exception e) {
            log.error("❌ Error marking user online: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void markUserOffline(User user) {
        try {
            // Update user entity
            user.setIsOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);

            // Remove from Redis
            String presenceKey = USER_PRESENCE_KEY_PREFIX + user.getPublicId();
            redisTemplate.delete(presenceKey);

            // Remove from online users set
            redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, user.getPublicId());

            log.info("🔴 User marked offline: {}", user.getPublicId());

            // Broadcast to all subscribers
            broadcastPresenceUpdate(user.getPublicId(), false);

        } catch (Exception e) {
            log.error("❌ Error marking user offline: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isUserOnline(String userId) {
        try {
            String presenceKey = USER_PRESENCE_KEY_PREFIX + userId;
            Boolean exists = redisTemplate.hasKey(presenceKey);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("❌ Error checking user online status: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAllOnlineUsers() {
        try {
            Set<String> onlineUsers = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
            if (onlineUsers == null || onlineUsers.isEmpty()) {
                return List.of();
            }
            
            // Verify each user's presence key still exists (defensive cleanup)
            return onlineUsers.stream()
                .filter(userId -> {
                    String presenceKey = USER_PRESENCE_KEY_PREFIX + userId;
                    Boolean hasKey = redisTemplate.hasKey(presenceKey);
                    
                    // If presence key expired but user still in set, remove them
                    if (hasKey == null || !hasKey) {
                        log.warn("🧹 Cleaning up expired presence for user: {}", userId);
                        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("❌ Error fetching online users: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void broadcastPresenceUpdate(String userId, boolean isOnline) {
        try {
            var update = new PresenceUpdateMessage(userId, isOnline, LocalDateTime.now().toString());
            messagingTemplate.convertAndSend("/topic/presence/updates", update);
            log.info("📢 Presence update broadcast: {} -> {}", userId, isOnline ? "ONLINE" : "OFFLINE");
        } catch (Exception e) {
            log.error("❌ Error broadcasting presence: {}", e.getMessage());
        }
    }

    /**
     * DTO for presence updates over WebSocket
     */
    public static class PresenceUpdateMessage {
        public String userId;
        public boolean isOnline;
        public String timestamp;

        public PresenceUpdateMessage(String userId, boolean isOnline, String timestamp) {
            this.userId = userId;
            this.isOnline = isOnline;
            this.timestamp = timestamp;
        }

        public String getUserId() {
            return userId;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
