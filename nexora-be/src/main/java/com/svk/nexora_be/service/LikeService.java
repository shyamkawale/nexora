package com.svk.nexora_be.service;

public interface LikeService {
    void likePost(String postId, String userId);
    void unlikePost(String postId, String userId);
    long getPostLikeCount(String postId);
    boolean isPostLikedByUser(String postId, String userId);

    void likeComment(String commentId, String userId);
    void unlikeComment(String commentId, String userId);
    long getCommentLikeCount(String commentId);
    boolean isCommentLikedByUser(String commentId, String userId);
}
