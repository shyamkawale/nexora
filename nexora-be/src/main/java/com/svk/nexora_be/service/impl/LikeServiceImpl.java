package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.entity.Comment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.PostLike;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.CommentRepository;
import com.svk.nexora_be.repository.PostLikeRepository;
import com.svk.nexora_be.repository.PostRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
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
        Post post = postRepository.findByPublicId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return postLikeRepository.countByPost(post);
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
    public void likeComment(String commentId, String userId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if already liked
        if (postLikeRepository.existsByCommentAndUser(comment, user)) {
            return;
        }

        PostLike like = PostLike.builder()
                .comment(comment)
                .user(user)
                .build();

        postLikeRepository.save(like);
    }

    @Override
    public void unlikeComment(String commentId, String userId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        postLikeRepository.findByCommentAndUser(comment, user)
                .ifPresent(postLikeRepository::delete);
    }

    @Override
    public long getCommentLikeCount(String commentId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return postLikeRepository.countByComment(comment);
    }

    @Override
    public boolean isCommentLikedByUser(String commentId, String userId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return postLikeRepository.existsByCommentAndUser(comment, user);
    }
}
