package com.svk.nexora_be.controller;

import com.svk.nexora_be.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/likes")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    // Post Likes
    @PostMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        likeService.likePost(postId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", true);
        response.put("likeCount", likeService.getPostLikeCount(postId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> unlikePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        likeService.unlikePost(postId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("likeCount", likeService.getPostLikeCount(postId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<Map<String, Object>> getPostLikeCount(@PathVariable String postId) {
        long count = likeService.getPostLikeCount(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", count);
        return ResponseEntity.ok(response);
    }

    // Comment Likes
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        likeService.likeComment(commentId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", true);
        response.put("likeCount", likeService.getCommentLikeCount(commentId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> unlikeComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        likeService.unlikeComment(commentId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("likeCount", likeService.getCommentLikeCount(commentId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<Map<String, Object>> getCommentLikeCount(@PathVariable String commentId) {
        long count = likeService.getCommentLikeCount(commentId);
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", count);
        return ResponseEntity.ok(response);
    }
}
