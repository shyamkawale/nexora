package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.LikeResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/likes")
@AllArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    // Post Likes
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        likeService.likePost(postId, userId);
        LikeResponse response = LikeResponse.builder()
                .liked(true)
                .likeCount(likeService.getPostLikeCount(postId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post liked"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> unlikePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        likeService.unlikePost(postId, userId);
        LikeResponse response = LikeResponse.builder()
                .liked(false)
                .likeCount(likeService.getPostLikeCount(postId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post unliked"));
    }

    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<ApiResponse<LikeResponse>> getPostLikeCount(@PathVariable String postId) {
        long count = likeService.getPostLikeCount(postId);
        LikeResponse response = LikeResponse.builder().likeCount(count).build();
        return ResponseEntity.ok(ApiResponse.success(response, "Like count fetched"));
    }

    // Comment Likes
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<LikeResponse>> likeComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        likeService.likeComment(commentId, userId);
        LikeResponse response = LikeResponse.builder()
                .liked(true)
                .likeCount(likeService.getCommentLikeCount(commentId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Comment liked"));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<LikeResponse>> unlikeComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        likeService.unlikeComment(commentId, userId);
        LikeResponse response = LikeResponse.builder()
                .liked(false)
                .likeCount(likeService.getCommentLikeCount(commentId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Comment unliked"));
    }

    @GetMapping("/comments/{commentId}/count")
    public ResponseEntity<ApiResponse<LikeResponse>> getCommentLikeCount(@PathVariable String commentId) {
        long count = likeService.getCommentLikeCount(commentId);
        LikeResponse response = LikeResponse.builder().likeCount(count).build();
        return ResponseEntity.ok(ApiResponse.success(response, "Like count fetched"));
    }
}
