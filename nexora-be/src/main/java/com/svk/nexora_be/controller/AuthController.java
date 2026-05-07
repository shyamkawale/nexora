package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.SignupRequest;
import com.svk.nexora_be.dto.request.LoginRequest;
import com.svk.nexora_be.dto.response.LoginApiResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AuthenticationServiceException;
import java.util.List;
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
    private final AuthenticationManager authenticationManager;

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

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginApiResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        final long ACCESS_TOKEN_EXPIRY_MINUTES = 15L;
        final long ACCESS_TOKEN_EXPIRY_SECONDS = ACCESS_TOKEN_EXPIRY_MINUTES * 60L;
        final long REFRESH_TOKEN_EXPIRY_MINUTES = 7 * 24 * 60L;

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Email and password are required"));
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            Authentication authResult = authenticationManager.authenticate(authenticationToken);

            if (!authResult.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failed"));
            }

            com.svk.nexora_be.entity.User user = (com.svk.nexora_be.entity.User) authResult.getPrincipal();
            List<String> roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

            String accessToken = jwtUtil.generateJwtToken(user, ACCESS_TOKEN_EXPIRY_MINUTES);
            response.addHeader("Authorization", "Bearer " + accessToken);

            String refreshToken = jwtUtil.generateJwtToken(user, REFRESH_TOKEN_EXPIRY_MINUTES);

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setSecure(false); // false for localhost; true in production
            cookie.setHttpOnly(true);
            // ensure cookie is sent to the refresh endpoint
            cookie.setPath("/api/v1/auth/refresh-token");
            response.addCookie(cookie);

            LoginApiResponse responseBody = LoginApiResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(ACCESS_TOKEN_EXPIRY_SECONDS)
                .user(LoginApiResponse.UserInfo.builder()
                    .id(user.getPublicId())
                    .username(user.getUsername())
                    .roles(roles)
                    .build())
                .build();

            return ResponseEntity.ok(ApiResponse.success(responseBody, "Login successful"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password"));
        } catch (ClassCastException ex) {
            throw new AuthenticationServiceException("Authenticated principal is not a User", ex);
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
            cookie.setPath("/api/v1/auth/refresh-token");
            cookie.setMaxAge(0); // delete cookie

            response.addCookie(cookie);

            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logout failed: " + e.getMessage()));
        }
    }
}
