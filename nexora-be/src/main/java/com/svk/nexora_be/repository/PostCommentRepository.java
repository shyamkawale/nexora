package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Optional<PostComment> findByPublicId(String publicId);
    Optional<PostComment> findByPostOrganizationIdAndPublicId(Long organizationId, String publicId);
    Page<PostComment> findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String postPublicId, Pageable pageable);
    Page<PostComment> findByPostOrganizationIdAndPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long organizationId,
            String postPublicId,
            Pageable pageable
    );
    List<PostComment> findByPostPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String postPublicId);
}
