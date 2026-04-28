package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.SignupRequest;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.AuthService;
import com.svk.nexora_be.service.PresenceService;
import com.svk.nexora_be.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.svk.nexora_be.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PresenceService presenceService;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
//        try {
//            AuthResponse response = authService.login(request);
//
//            // Mark user as online after successful login
//            if (response.getToken() != null && response.getUser() != null) {
//                User user = userService.getUserByPublicId(response.getUser().getPublicId());
//                if (user != null) {
//                    presenceService.markUserOnline(user);
//                }
//            }
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignupRequest request) {
        try {
            // Hash the password before saving
            request.setPassword(passwordEncoder.encode(request.getPassword()));

            authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(null, "User created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        try {
            String userId = jwtUtil.getCurrentUserId();

            if (userId != null) {
                User user = userService.getUserByPublicId(userId);
                if (user != null) {
                    presenceService.markUserOffline(user);
                }
            }

            // Delete refresh token cookie
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production (HTTPS)
            cookie.setPath("/refresh-token");
            cookie.setMaxAge(0); // delete cookie

            response.addCookie(cookie);

            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logout failed: " + e.getMessage()));
        }
    }
}
