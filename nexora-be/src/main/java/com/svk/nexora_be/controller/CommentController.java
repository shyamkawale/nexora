package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreateCommentRequest;
import com.svk.nexora_be.dto.response.CommentResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.security.JwtUtil;
import com.svk.nexora_be.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(@RequestBody CreateCommentRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        CommentResponse comment = commentService.createComment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(comment, "Comment created"));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        CommentResponse comment = commentService.getComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(comment, "Comment fetched"));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getPostComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> comments = commentService.getPostComments(postId, userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments, "Comments fetched"));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String commentId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted"));
    }
}
