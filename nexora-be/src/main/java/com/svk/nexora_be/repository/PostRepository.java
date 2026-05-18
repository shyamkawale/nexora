package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * NOTE: Do NOT add Spring Cache annotations (e.g. @Cacheable) to repository
 * methods that return JPA entities. Caching managed entities serializes
 * Hibernate proxies (HibernateProxy) and lazy collections (PersistentBag)
 * into Redis. On a cache hit those come back detached (no Session) and any
 * subsequent write that touches them throws:
 *   "Cannot lazily initialize collection (no session)"
 * If caching is desired, cache DTOs at the service layer instead.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPublicId(String publicId);

    Optional<Post> findByOrganizationIdAndPublicId(Long organizationId, String publicId);

    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByOrganizationIdAndIsActiveTrueOrderByCreatedAtDesc(Long organizationId, Pageable pageable);

    List<Post> findByAuthorPublicIdAndIsActiveTrueOrderByCreatedAtDesc(String authorPublicId);

    List<Post> findByOrganizationIdAndAuthorPublicIdAndIsActiveTrueOrderByCreatedAtDesc(
            Long organizationId,
            String authorPublicId
    );
}
