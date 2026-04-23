package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByPublicId(String publicId);
    Optional<Group> findById(Long id);
}
