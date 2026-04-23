package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.CreateCommentRequest;
import com.svk.nexora_be.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse createComment(String userId, CreateCommentRequest request);
    CommentResponse getComment(String commentId, String currentUserId);
    Page<CommentResponse> getPostComments(String postId, String currentUserId, Pageable pageable);
    void deleteComment(String commentId, String userId);
}
