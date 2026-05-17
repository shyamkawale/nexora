package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.PostComment;
import com.svk.nexora_be.entity.Post;
import com.svk.nexora_be.entity.PostLike;
import com.svk.nexora_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Optional<PostLike> findByCommentAndUser(PostComment comment, User user);
    List<PostLike> findByComment(PostComment comment);
    long countByComment(PostComment comment);

    // Check if user liked
    boolean existsByPostAndUser(Post post, User user);
    boolean existsByCommentAndUser(PostComment comment, User user);

    // Count by publicId to avoid lazy loading
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.publicId = :postPublicId")
    long countByPostPublicId(@Param("postPublicId") String postPublicId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.comment.publicId = :commentPublicId")
    long countByCommentPublicId(@Param("commentPublicId") String commentPublicId);
}
