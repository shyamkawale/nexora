package com.svk.nexora_be.service;

public interface PostLikeService {
    void likePost(String postId, String userId);
    void unlikePost(String postId, String userId);
    long getPostLikeCount(String postId);
    boolean isPostLikedByUser(String postId, String userId);

    void likePostComment(String postCommentId, String userId);
    void unlikePostComment(String postCommentId, String userId);
    long getPostCommentLikeCount(String postCommentId);
    boolean isPostCommentLikedByUser(String postCommentId, String userId);
}
