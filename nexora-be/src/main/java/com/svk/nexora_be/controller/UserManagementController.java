package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        try {
            org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest.of(page, size);
            List<UserResponse> userResponses = userService.getAllUsers(pageable);
            return ResponseEntity.ok(ApiResponse.success(userResponses, "Users fetched"));
        } catch (Exception e) {
            log.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }

    /**
     * Get user by ID (ADMIN only)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(UserResponse.fromUser(user), "User fetched"));
        } catch (Exception e) {
            log.error("Error fetching user by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }

    /**
     * Delete user (ADMIN only)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
            }
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * Deactivate user (ADMIN only)
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
            }
            user.setIsActive(false);
            userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
        } catch (Exception e) {
            log.error("Failed to deactivate user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to deactivate user: " + e.getMessage()));
        }
    }

    /**
     * Activate user (ADMIN only)
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable String userId) {
        try {
            User user = userService.getUserByPublicId(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "User not found"));
            }
            user.setIsActive(true);
            userService.updateUser(user);
            return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
        } catch (Exception e) {
            log.error("Failed to activate user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to activate user: " + e.getMessage()));
        }
    }
}
