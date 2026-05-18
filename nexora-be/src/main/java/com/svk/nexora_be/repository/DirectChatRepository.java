package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.DirectChat;
import com.svk.nexora_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectChatRepository extends JpaRepository<DirectChat, Long> {
    Optional<DirectChat> findByPublicId(String publicId);

    Optional<DirectChat> findByOrganizationIdAndPublicId(Long organizationId, String publicId);

    @Query("SELECT c FROM DirectChat c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<DirectChat> findChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT c FROM DirectChat c WHERE c.organization.id = :organizationId AND ((c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1))")
    Optional<DirectChat> findChatBetweenUsersInOrganization(
            @Param("organizationId") Long organizationId,
            @Param("user1") User user1,
            @Param("user2") User user2
    );

    @Query("SELECT c FROM DirectChat c WHERE c.user1 = :user OR c.user2 = :user ORDER BY c.updatedAt DESC")
    List<DirectChat> findAllChatsForUser(@Param("user") User user);

    @Query("SELECT c FROM DirectChat c WHERE c.organization.id = :organizationId AND (c.user1 = :user OR c.user2 = :user) ORDER BY c.updatedAt DESC")
    List<DirectChat> findAllChatsForUserInOrganization(
            @Param("organizationId") Long organizationId,
            @Param("user") User user
    );
}
