package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.CreateCommentRequest;
import com.svk.nexora_be.dto.response.CommentResponse;
import com.svk.nexora_be.entity.Comment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.CommentRepository;
import com.svk.nexora_be.repository.PostLikeRepository;
import com.svk.nexora_be.repository.PostRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponse createComment(String userId, CreateCommentRequest request) {
        User author = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = postRepository.findByPublicId(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = Comment.builder()
                .author(author)
                .post(post)
                .content(request.getContent())
                .isActive(true)
                .build();

        comment = commentRepository.save(comment);
        return CommentResponse.fromComment(comment, 0, false);
    }

    @Override
    public CommentResponse getComment(String commentId, String currentUserId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        long likeCount = postLikeRepository.countByComment(comment);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);
        boolean likedByCurrentUser = currentUser != null && 
                postLikeRepository.existsByCommentAndUser(comment, currentUser);

        return CommentResponse.fromComment(comment, likeCount, likedByCurrentUser);
    }

    @Override
    public Page<CommentResponse> getPostComments(String postId, String currentUserId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(postId, pageable);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);

        return comments.map(comment -> {
            long likeCount = postLikeRepository.countByComment(comment);
            boolean likedByCurrentUser = currentUser != null && 
                    postLikeRepository.existsByCommentAndUser(comment, currentUser);
            return CommentResponse.fromComment(comment, likeCount, likedByCurrentUser);
        });
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findByPublicId(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        comment.setIsActive(false);
        commentRepository.save(comment);
    }
}
