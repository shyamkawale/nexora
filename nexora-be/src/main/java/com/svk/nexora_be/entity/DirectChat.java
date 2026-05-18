package com.svk.nexora_be.entity;

import com.svk.nexora_be.entity.base.BaseChat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "direct_chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DirectChat extends BaseChat {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    public User getAnotherUser(User currentUser) {
        if (user1.getId().equals(currentUser.getId())) {
            return user2;
        }
        return user1;
    }

    public boolean hasParticipant(String userId) {
        return user1.getPublicId().equals(userId) || user2.getPublicId().equals(userId);
    }
}
