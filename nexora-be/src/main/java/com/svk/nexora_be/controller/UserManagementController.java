package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserService userService;

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        try {
            org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest.of(page, size);
            List<UserResponse> userResponses = userService.getAllUsers(pageable);
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user by ID (ADMIN only)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete user (ADMIN only)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            userService.deleteUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to delete user: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Deactivate user (ADMIN only)
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setIsActive(false);
            userService.updateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to deactivate user: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Activate user (ADMIN only)
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setIsActive(true);
            userService.updateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User activated successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to activate user: " + e.getMessage());
            error.put("success", false);
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
