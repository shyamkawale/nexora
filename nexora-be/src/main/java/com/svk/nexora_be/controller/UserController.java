package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.service.PresenceService;
import com.svk.nexora_be.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PresenceService presenceService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/v1/user/info")
    public ResponseEntity<UserResponse> getUserInfo() {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponse user = userService.getUserInfo(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/v1/user/profile/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        UserResponse user = userService.getUserInfo(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/v1/user/presence")
    public ResponseEntity<Map<String, Object>> updatePresence(@RequestBody Map<String, Object> presenceData) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            User user = userService.getUserByPublicId(userId);
            if (user != null) {
                // Mark user as online
                presenceService.markUserOnline(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("userId", userId);
                response.put("status", "online");
                response.put("message", "Presence updated successfully");
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update presence: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        Pageable pageable = PageRequest.of(page, size);
        List<UserResponse> users = userService.getAllUsers(pageable);
        
        // Filter out current user from results
        users.removeIf(user -> user.getPublicId().equals(currentUserId));
        
        return ResponseEntity.ok(users);
    }

    @GetMapping("/api/v1/users/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        List<UserResponse> users = userService.searchUsers(q);
        
        // Filter out current user from results
        users.removeIf(user -> user.getPublicId().equals(currentUserId));
        
        return ResponseEntity.ok(users);
    }
}
