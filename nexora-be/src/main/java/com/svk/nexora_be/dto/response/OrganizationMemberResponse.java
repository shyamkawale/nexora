package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.OrganizationMember;
import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.enums.OrganizationRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMemberResponse {
    private String organizationPublicId;
    private UserResponse user;
    private OrganizationRole role;
    private OrganizationMemberStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    public static OrganizationMemberResponse fromMembership(OrganizationMember membership) {
        return OrganizationMemberResponse.builder()
                .organizationPublicId(membership.getOrganization().getPublicId())
                .user(UserResponse.fromUser(membership.getUser()))
                .role(membership.getRole())
                .status(membership.getStatus())
                .joinedAt(membership.getJoinedAt())
                .createdAt(membership.getCreatedAt())
                .build();
    }
}
