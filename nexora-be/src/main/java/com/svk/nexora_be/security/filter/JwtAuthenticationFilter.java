package com.svk.nexora_be.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svk.nexora_be.dto.response.LoginApiResponse;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.security.JwtUtil;
import com.svk.nexora_be.dto.request.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final long ACCESS_TOKEN_EXPIRY_MINUTES = 15L;
    private static final long ACCESS_TOKEN_EXPIRY_SECONDS = ACCESS_TOKEN_EXPIRY_MINUTES * 60L;
    private static final long REFRESH_TOKEN_EXPIRY_MINUTES = 7 * 24 * 60L;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!request.getServletPath().equals("/api/v1/auth/login") || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException ex) {
            writeUnauthorizedResponse(response, objectMapper, "Invalid login request payload");
            return;
        }

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            writeUnauthorizedResponse(response, objectMapper, "Email and password are required");
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            Authentication authResult = authenticationManager.authenticate(authenticationToken);

            if (!authResult.isAuthenticated()) {
                writeUnauthorizedResponse(response, objectMapper, "Authentication failed");
                return;
            }

            User user = (User) authResult.getPrincipal();
            List<String> roles = authResult.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtUtil.generateJwtToken(user, ACCESS_TOKEN_EXPIRY_MINUTES);
            response.addHeader("Authorization", "Bearer " + accessToken);

            String refreshToken = jwtUtil.generateJwtToken(user, REFRESH_TOKEN_EXPIRY_MINUTES); // 7days

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setSecure(false); // FIX for localhost (as refresh token was not coming in Chrome browser)
            cookie.setHttpOnly(true);
            cookie.setPath("/refresh-token");
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

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), responseBody);
            return;
        } catch (AuthenticationException ex) {
            writeUnauthorizedResponse(response, objectMapper, "Invalid email or password");
            return;
        } catch (ClassCastException ex) {
            throw new AuthenticationServiceException("Authenticated principal is not a User", ex);
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, ObjectMapper objectMapper, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), java.util.Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", message
        ));
    }
}
