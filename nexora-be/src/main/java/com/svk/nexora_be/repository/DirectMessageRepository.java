package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.DirectMessage;
import com.svk.nexora_be.entity.DirectMessageChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    Page<DirectMessage> findByChat(DirectMessageChat chat, Pageable pageable);
    Page<DirectMessage> findByChatOrderByCreatedAtDesc(DirectMessageChat chat, Pageable pageable);
}
