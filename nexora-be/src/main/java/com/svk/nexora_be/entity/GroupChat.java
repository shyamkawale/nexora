package com.svk.nexora_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svk.nexora_be.entity.base.BaseChat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "group_chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GroupChat extends BaseChat {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 255)
    private String groupName;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<GroupChatMember> members;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public boolean hasParticipant(String currentUserId) {
        return members.stream()
                .anyMatch(member -> member.getUser().getPublicId().equals(currentUserId) && member.getIsActive());
    }
}
