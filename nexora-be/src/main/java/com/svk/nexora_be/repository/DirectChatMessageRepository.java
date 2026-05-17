package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.DirectChatMessage;
import com.svk.nexora_be.entity.DirectChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectChatMessageRepository extends JpaRepository<DirectChatMessage, Long> {
    Page<DirectChatMessage> findByChat(DirectChat chat, Pageable pageable);
    Page<DirectChatMessage> findByChatOrderByCreatedAtDesc(DirectChat chat, Pageable pageable);
}
