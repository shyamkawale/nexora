package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.CreatePostCommentRequest;
import com.svk.nexora_be.dto.response.PostCommentResponse;
import com.svk.nexora_be.entity.PostComment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.PostCommentRepository;
import com.svk.nexora_be.repository.PostLikeRepository;
import com.svk.nexora_be.repository.PostRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PostCommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Override
    public PostCommentResponse createPostComment(String userId, CreatePostCommentRequest request) {
        User author = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = postRepository.findByPublicId(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        PostComment postComment = PostComment.builder()
                .author(author)
                .post(post)
                .content(request.getContent())
                .isActive(true)
                .build();

        postComment = postCommentRepository.save(postComment);
        
        // Force eager load of author before returning to avoid lazy initialization outside transaction
        postComment.getAuthor().getId();
        
        return PostCommentResponse.fromPostComment(postComment, 0, false);
    }

    @Override
    public PostCommentResponse getPostComment(String postCommentId, String currentUserId) {
        PostComment postComment = postCommentRepository.findByPublicId(postCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));

        // Force eager load of author before returning to avoid lazy initialization outside transaction
        postComment.getAuthor().getId();
        
        long likeCount = postLikeRepository.countByCommentPublicId(postCommentId);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);
        boolean likedByCurrentUser = currentUser != null && 
                postLikeRepository.existsByCommentAndUser(postComment, currentUser);

        return PostCommentResponse.fromPostComment(postComment, likeCount, likedByCurrentUser);
    }

    @Override
    public Page<PostCommentResponse> getPostComments(String postId, String currentUserId, Pageable pageable) {
        Page<PostComment> comments = postCommentRepository.findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(postId, pageable);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);

        return comments.map(comment -> {
            // Force eager load of author to avoid lazy initialization outside transaction
            comment.getAuthor().getId();
            
            long likeCount = postLikeRepository.countByCommentPublicId(comment.getPublicId());
            boolean likedByCurrentUser = currentUser != null && 
                    postLikeRepository.existsByCommentAndUser(comment, currentUser);
            return PostCommentResponse.fromPostComment(comment, likeCount, likedByCurrentUser);
        });
    }

    @Override
    public void deletePostComment(String postCommentId, String userId) {
        PostComment postComment = postCommentRepository.findByPublicId(postCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!postComment.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        postComment.setIsActive(false);
        postCommentRepository.save(postComment);
    }
}
