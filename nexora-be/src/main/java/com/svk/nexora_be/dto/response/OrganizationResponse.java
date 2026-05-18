package com.svk.nexora_be.dto.response;

import com.svk.nexora_be.entity.Organization;
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
public class OrganizationResponse {
    private String publicId;
    private String name;
    private String description;
    private Boolean isActive;
    private OrganizationRole role;
    private OrganizationMemberStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrganizationResponse fromOrganization(Organization organization) {
        return OrganizationResponse.builder()
                .publicId(organization.getPublicId())
                .name(organization.getName())
                .description(organization.getDescription())
                .isActive(organization.getIsActive())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }

    public static OrganizationResponse fromMembership(OrganizationMember membership) {
        Organization organization = membership.getOrganization();
        return OrganizationResponse.builder()
                .publicId(organization.getPublicId())
                .name(organization.getName())
                .description(organization.getDescription())
                .isActive(organization.getIsActive())
                .role(membership.getRole())
                .status(membership.getStatus())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
}
