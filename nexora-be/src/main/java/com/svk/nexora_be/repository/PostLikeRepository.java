package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Comment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.PostLike;
import com.svk.nexora_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // Post likes
    Optional<PostLike> findByPostAndUser(Post post, User user);
    List<PostLike> findByPost(Post post);
    long countByPost(Post post);

    // Comment likes
    Optional<PostLike> findByCommentAndUser(Comment comment, User user);
    List<PostLike> findByComment(Comment comment);
    long countByComment(Comment comment);

    // Check if user liked
    boolean existsByPostAndUser(Post post, User user);
    boolean existsByCommentAndUser(Comment comment, User user);
}
