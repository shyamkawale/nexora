package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPublicId(String publicId);
    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByAuthorPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String authorPublicId);
}
