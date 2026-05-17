package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreatePostCommentRequest;
import com.svk.nexora_be.dto.response.PostCommentResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.security.JwtUtil;
import com.svk.nexora_be.service.PostCommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post-comments")
@AllArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCommentResponse>> createPostComment(@RequestBody CreatePostCommentRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        PostCommentResponse postComment = postCommentService.createPostComment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(postComment, "Post comment created"));
    }

    @GetMapping("/{postCommentId}")
    public ResponseEntity<ApiResponse<PostCommentResponse>> getPostComment(@PathVariable String postCommentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        PostCommentResponse postComment = postCommentService.getPostComment(postCommentId, userId);
        return ResponseEntity.ok(ApiResponse.success(postComment, "Post comment fetched"));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Page<PostCommentResponse>>> getPostComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<PostCommentResponse> postComments = postCommentService.getPostComments(postId, userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(postComments, "Post comments fetched"));
    }

    @DeleteMapping("/{postCommentId}")
    public ResponseEntity<ApiResponse<Void>> deletePostComment(@PathVariable String postCommentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postCommentService.deletePostComment(postCommentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Post comment deleted"));
    }
}
