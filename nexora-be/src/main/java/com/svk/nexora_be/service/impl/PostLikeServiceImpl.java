package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.entity.PostComment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.PostLike;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.PostCommentRepository;
import com.svk.nexora_be.repository.PostLikeRepository;
import com.svk.nexora_be.repository.PostRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PostLikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    @Override
    public void likePost(String postId, String userId) {
        Post post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if already liked
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            return;
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);
    }

    @Override
    public void unlikePost(String postId, String userId) {
        Post post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        postLikeRepository.findByPostAndUser(post, user)
                .ifPresent(postLikeRepository::delete);
    }

    @Override
    public long getPostLikeCount(String postId) {
        return postLikeRepository.countByPostPublicId(postId);
    }

    @Override
    public boolean isPostLikedByUser(String postId, String userId) {
        Post post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return postLikeRepository.existsByPostAndUser(post, user);
    }

    @Override
    public void likePostComment(String postCommentId, String userId) {
        PostComment postComment = postCommentRepository.findByPublicId(postCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if already liked
        if (postLikeRepository.existsByCommentAndUser(postComment, user)) {
            return;
        }

        PostLike like = PostLike.builder()
                .comment(postComment)
                .user(user)
                .build();

        postLikeRepository.save(like);
    }

    @Override
    public void unlikePostComment(String postCommentId, String userId) {
        PostComment postComment = postCommentRepository.findByPublicId(postCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        postLikeRepository.findByCommentAndUser(postComment, user)
                .ifPresent(postLikeRepository::delete);
    }

    @Override
    public long getPostCommentLikeCount(String postCommentId) {
        return postLikeRepository.countByCommentPublicId(postCommentId);
    }

    @Override
    public boolean isPostCommentLikedByUser(String postCommentId, String userId) {
        PostComment postComment = postCommentRepository.findByPublicId(postCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return postLikeRepository.existsByCommentAndUser(postComment, user);
    }
}
