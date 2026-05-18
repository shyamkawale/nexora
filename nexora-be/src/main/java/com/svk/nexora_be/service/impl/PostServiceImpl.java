package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.CreatePostRequest;
import com.svk.nexora_be.dto.response.PostResponse;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.OrganizationRepository;
import com.svk.nexora_be.repository.PostLikeRepository;
import com.svk.nexora_be.repository.PostRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.PostService;
import com.svk.nexora_be.tenant.OrganizationContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public PostResponse createPost(String userId, CreatePostRequest request) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        User author = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .author(author)
                .organization(organizationRepository.getReferenceById(organizationId))
                .content(request.getContent())
                .isActive(true)
                .build();

        post = postRepository.save(post);
        return PostResponse.fromPost(post, 0, 0, false);
    }

    @Override
    public PostResponse getPost(String postId, String currentUserId) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        Post post = postRepository.findByOrganizationIdAndPublicId(organizationId, postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        long likeCount = postLikeRepository.countByPost(post);
        long commentCount = post.getComments() != null ? post.getComments().size() : 0;
        
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);
        boolean likedByCurrentUser = currentUser != null && 
                postLikeRepository.existsByPostAndUser(post, currentUser);

        return PostResponse.fromPost(post, likeCount, commentCount, likedByCurrentUser);
    }

    @Override
    public Page<PostResponse> getAllPosts(String currentUserId, Pageable pageable) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        Page<Post> posts = postRepository.findByOrganizationIdAndIsActiveTrueOrderByCreatedAtDesc(organizationId, pageable);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);

        return posts.map(post -> {
            long likeCount = postLikeRepository.countByPost(post);
            long commentCount = post.getComments() != null ? post.getComments().size() : 0;
            boolean likedByCurrentUser = currentUser != null && 
                    postLikeRepository.existsByPostAndUser(post, currentUser);
            return PostResponse.fromPost(post, likeCount, commentCount, likedByCurrentUser);
        });
    }

    @Override
    public List<PostResponse> getUserPosts(String userPublicId, String currentUserId) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        List<Post> posts = postRepository
                .findByOrganizationIdAndAuthorPublicIdAndIsActiveTrueOrderByCreatedAtDesc(organizationId, userPublicId);
        User currentUser = userRepository.findByPublicId(currentUserId).orElse(null);

        return posts.stream().map(post -> {
            long likeCount = postLikeRepository.countByPost(post);
            long commentCount = post.getComments() != null ? post.getComments().size() : 0;
            boolean likedByCurrentUser = currentUser != null && 
                    postLikeRepository.existsByPostAndUser(post, currentUser);
            return PostResponse.fromPost(post, likeCount, commentCount, likedByCurrentUser);
        }).collect(Collectors.toList());
    }

    @Override
    public void deletePost(String postId, String userId) {
        Long organizationId = OrganizationContextHolder.requireOrganizationId();
        Post post = postRepository.findByOrganizationIdAndPublicId(organizationId, postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }

        post.setIsActive(false);
        postRepository.save(post);
    }
}
