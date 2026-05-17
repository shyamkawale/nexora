package com.svk.nexora_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svk.nexora_be.entity.base.BaseMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "group_chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GroupChatMessage extends BaseMessage {
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private GroupChat groupChat;
}
