package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.CreatePostCommentRequest;
import com.svk.nexora_be.dto.response.PostCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCommentService {
    PostCommentResponse createPostComment(String userId, CreatePostCommentRequest request);
    PostCommentResponse getPostComment(String postCommentId, String currentUserId);
    Page<PostCommentResponse> getPostComments(String postId, String currentUserId, Pageable pageable);
    void deletePostComment(String postCommentId, String userId);
}
