package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Group;
import com.svk.nexora_be.entity.GroupChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage, Long> {
    Page<GroupChatMessage> findByGroupOrderByCreatedAtDesc(Group group, Pageable pageable);
    long countByGroupAndMessageStatus(Group group, GroupChatMessage.MessageStatus status);
}
