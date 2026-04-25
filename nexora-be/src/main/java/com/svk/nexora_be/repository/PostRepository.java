package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Post;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Cacheable(cacheNames = "posts", key = "'post:' + #p0")
    Optional<Post> findByPublicId(String publicId);

    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Cacheable(cacheNames = "postsByAuthor", key = "'author:' + #p0")
    List<Post> findByAuthorPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String authorPublicId);
}
