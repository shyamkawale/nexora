package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.CreatePostRequest;
import com.svk.nexora_be.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    PostResponse createPost(String userId, CreatePostRequest request);
    PostResponse getPost(String postId, String currentUserId);
    Page<PostResponse> getAllPosts(String currentUserId, Pageable pageable);
    List<PostResponse> getUserPosts(String userPublicId, String currentUserId);
    void deletePost(String postId, String userId);
}
