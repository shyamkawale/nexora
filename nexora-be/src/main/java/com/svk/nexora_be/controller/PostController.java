package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreatePostRequest;
import com.svk.nexora_be.dto.response.PostResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody CreatePostRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        PostResponse post = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(post, "Post created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> posts = postService.getAllPosts(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(posts, "Posts fetched"));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        PostResponse post = postService.getPost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(post, "Post fetched"));
    }

    @GetMapping("/user/{userPublicId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getUserPosts(@PathVariable String userPublicId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        List<PostResponse> posts = postService.getUserPosts(userPublicId, userId);
        return ResponseEntity.ok(ApiResponse.success(posts, "User posts fetched"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable String postId) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Post deleted"));
    }
}
