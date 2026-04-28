package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.PresenceUpdateResponse;
import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.PresenceService;
import com.svk.nexora_be.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PresenceService presenceService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/v1/user/info")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo() {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        UserResponse user = userService.getUserInfo(userId);
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success(user, "User info fetched"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
    }

    @GetMapping("/api/v1/user/profile/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable String userId) {
        UserResponse user = userService.getUserInfo(userId);
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success(user, "User profile fetched"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
    }

    @PostMapping("/api/v1/user/presence")
    public ResponseEntity<ApiResponse<PresenceUpdateResponse>> updatePresence(@RequestBody java.util.Map<String, Object> presenceData) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        User user = userService.getUserByPublicId(userId);
        if (user != null) {
            // Mark user as online
            presenceService.markUserOnline(user);

            PresenceUpdateResponse dto = PresenceUpdateResponse.builder()
                    .userId(userId)
                    .status("online")
                    .message("Presence updated successfully")
                    .build();

            return ResponseEntity.ok(ApiResponse.success(dto, "Presence updated"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        Pageable pageable = PageRequest.of(page, size);
        List<UserResponse> users = userService.getAllUsers(pageable);

        // Filter out current user from results
        users.removeIf(user -> user.getPublicId().equals(currentUserId));

        return ResponseEntity.ok(ApiResponse.success(users, "Users fetched"));
    }

    @GetMapping("/api/v1/users/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String q) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        List<UserResponse> users = userService.searchUsers(q);

        // Filter out current user from results
        users.removeIf(user -> user.getPublicId().equals(currentUserId));

        return ResponseEntity.ok(ApiResponse.success(users, "Search results"));
    }
}
