package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.response.PostLikeResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.PostLikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

@RestController
@RequestMapping("/api/v1/likes")
@AllArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final JwtUtil jwtUtil;

    // Post Likes
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostLikeResponse>> likePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postLikeService.likePost(postId, userId);
        PostLikeResponse response = PostLikeResponse.builder()
                .liked(true)
                .likeCount(postLikeService.getPostLikeCount(postId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post liked"));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostLikeResponse>> unlikePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postLikeService.unlikePost(postId, userId);
        PostLikeResponse response = PostLikeResponse.builder()
                .liked(false)
                .likeCount(postLikeService.getPostLikeCount(postId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post unliked"));
    }

    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<ApiResponse<PostLikeResponse>> getPostLikeCount(@PathVariable String postId) {
        long count = postLikeService.getPostLikeCount(postId);
        PostLikeResponse response = PostLikeResponse.builder().likeCount(count).build();
        return ResponseEntity.ok(ApiResponse.success(response, "Like count fetched"));
    }

    // Post Comment Likes
    @PostMapping("/post-comments/{postCommentId}")
    public ResponseEntity<ApiResponse<PostLikeResponse>> likePostComment(@PathVariable String postCommentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postLikeService.likePostComment(postCommentId, userId);
        PostLikeResponse response = PostLikeResponse.builder()
                .liked(true)
                .likeCount(postLikeService.getPostCommentLikeCount(postCommentId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post comment liked"));
    }

    @DeleteMapping("/post-comments/{postCommentId}")
    public ResponseEntity<ApiResponse<PostLikeResponse>> unlikePostComment(@PathVariable String postCommentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postLikeService.unlikePostComment(postCommentId, userId);
        PostLikeResponse response = PostLikeResponse.builder()
                .liked(false)
                .likeCount(postLikeService.getPostCommentLikeCount(postCommentId))
                .build();
        return ResponseEntity.ok(ApiResponse.success(response, "Post comment unliked"));
    }

    @GetMapping("/post-comments/{postCommentId}/count")
    public ResponseEntity<ApiResponse<PostLikeResponse>> getPostCommentLikeCount(@PathVariable String postCommentId) {
        long count = postLikeService.getPostCommentLikeCount(postCommentId);
        PostLikeResponse response = PostLikeResponse.builder().likeCount(count).build();
        return ResponseEntity.ok(ApiResponse.success(response, "Like count fetched"));
    }
}
