package com.svk.nexora_be.entity;

import com.svk.nexora_be.entity.base.BaseMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "direct_chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DirectChatMessage extends BaseMessage {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private DirectChat chat;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;
}
