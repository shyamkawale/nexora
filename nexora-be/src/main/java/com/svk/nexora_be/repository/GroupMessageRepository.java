package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    Page<GroupMessage> findByGroupChatPublicIdOrderByCreatedAtDesc(String groupChatPublicId, Pageable pageable);
}
