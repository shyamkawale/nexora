package com.svk.nexora_be.security;

import com.svk.nexora_be.entity.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Component
public class JwtUtil {
    @Value("${jwt.secret:mysecretkeythatismoretha256bitslongandsecure12345678901234567890}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    private Key hmacKey;

    @PostConstruct
    public void init() {
        this.hmacKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(String username, long expirationTimeInMinutes) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtToken(User user, long expirationTimeInMinutes) {
        return Jwts.builder()
                .setSubject(user.getPublicId())
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "username", user.getUsername(),
                        "roles", user.getRole()
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndExtractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null; // Invalid or expired JWT
        }
    }

    // new methods:
    public String getCurrentUserId() {
        String token = extractTokenFromRequest();
        return validateAndExtractUsername(token);
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(secretKey.getBytes());
//    }

//    public String generateToken(String userId, String email) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
//        claims.put("email", email);
//        return createToken(claims, userId);
//    }
//
//    public String generateToken(String userId, String email, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
//        claims.put("email", email);
//        claims.put("role", role);
//        return createToken(claims, userId);
//    }

//    private String createToken(Map<String, Object> claims, String subject) {
//        long now = System.currentTimeMillis();
//        long expirationTimeMs = now + expirationTime;
//
//        return Jwts.builder()
//                .claims(claims)
//                .subject(subject)
//                .issuedAt(new Date(now))
//                .expiration(new Date(expirationTimeMs))
//                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
//                .compact();
//    }

//    public String extractUserId(String token) {
//        return extractAllClaims(token).getSubject();
//    }

//    public Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }

//    public Boolean isTokenExpired(String token) {
//        try {
//            Claims claims = extractAllClaims(token);
//            return claims.getExpiration().before(new Date());
//        } catch (Exception e) {
//            return true;
//        }
//    }

//    public Boolean validateToken(String token) {
//        try {
//            extractAllClaims(token);
//            return !isTokenExpired(token);
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
