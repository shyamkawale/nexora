package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    Optional<GroupChat> findByPublicId(String publicId);

    @Query("SELECT gc FROM GroupChat gc JOIN gc.members gcm WHERE gcm.user.publicId = :userPublicId AND gcm.isActive = true")
    List<GroupChat> findActiveGroupsForUser(@Param("userPublicId") String userPublicId);
}
