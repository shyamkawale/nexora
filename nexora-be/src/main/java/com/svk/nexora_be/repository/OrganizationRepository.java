package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByPublicId(String publicId);

    List<Organization> findByIsActiveTrueOrderByNameAsc();
}
