package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable(cacheNames = "users", key = "'email:' + #p0")
    Optional<User> findByEmail(String email);

    @Cacheable(cacheNames = "users", key = "'username:' + #p0")
    Optional<User> findByUsername(String username);

    @Cacheable(cacheNames = "users", key = "'publicId:' + #p0")
    Optional<User> findByPublicId(String publicId);

    boolean existsByEmail(String email);
}
