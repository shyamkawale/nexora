package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByPublicId(String publicId);
    Page<Comment> findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String postPublicId, Pageable pageable);
    List<Comment> findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String postPublicId);
}
