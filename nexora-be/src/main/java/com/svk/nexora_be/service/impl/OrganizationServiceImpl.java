package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.CreateOrganizationRequest;
import com.svk.nexora_be.dto.response.OrganizationMemberResponse;
import com.svk.nexora_be.dto.response.OrganizationResponse;
import com.svk.nexora_be.entity.Organization;
import com.svk.nexora_be.entity.OrganizationMember;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.enums.OrganizationRole;
import com.svk.nexora_be.enums.UserRole;
import com.svk.nexora_be.exception.ForbiddenException;
import com.svk.nexora_be.exception.NotFoundException;
import com.svk.nexora_be.repository.OrganizationMemberRepository;
import com.svk.nexora_be.repository.OrganizationRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    @Override
    public OrganizationResponse createOrganization(String creatorPublicId, CreateOrganizationRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Organization name is required");
        }

        User creator = getUserOrThrow(creatorPublicId);
        ensureGlobalAdmin(creator);
        Organization organization = Organization.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .isActive(true)
                .build();
        organization = organizationRepository.save(organization);

        OrganizationMember creatorMembership = OrganizationMember.builder()
                .organization(organization)
                .user(creator)
                .role(OrganizationRole.ADMIN)
                .status(OrganizationMemberStatus.APPROVED)
                .joinedAt(LocalDateTime.now())
                .build();
        organizationMemberRepository.save(creatorMembership);

        return OrganizationResponse.fromMembership(creatorMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getMyOrganizations(String userPublicId) {
        User user = getUserOrThrow(userPublicId);
        if (user.getRole() == UserRole.ADMIN) {
            return organizationRepository.findByIsActiveTrueOrderByNameAsc()
                    .stream()
                    .map(organization -> OrganizationResponse.builder()
                            .publicId(organization.getPublicId())
                            .name(organization.getName())
                            .description(organization.getDescription())
                            .isActive(organization.getIsActive())
                            .role(OrganizationRole.ADMIN)
                            .status(OrganizationMemberStatus.APPROVED)
                            .createdAt(organization.getCreatedAt())
                            .updatedAt(organization.getUpdatedAt())
                            .build())
                    .toList();
        }

        return organizationMemberRepository.findByUserPublicIdOrderByCreatedAtDesc(userPublicId)
                .stream()
                .map(OrganizationResponse::fromMembership)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getActiveOrganizations() {
        return organizationRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(OrganizationResponse::fromOrganization)
                .toList();
    }

    @Override
    public OrganizationMemberResponse requestToJoin(String userPublicId, String organizationPublicId) {
        User user = getUserOrThrow(userPublicId);
        Organization organization = getOrganizationOrThrow(organizationPublicId);

        OrganizationMember membership = organizationMemberRepository.findByOrganizationAndUser(organization, user)
                .map(existing -> {
                    if (existing.getStatus() == OrganizationMemberStatus.REJECTED) {
                        existing.setStatus(OrganizationMemberStatus.PENDING);
                        existing.setRole(OrganizationRole.MEMBER);
                        existing.setJoinedAt(null);
                    }
                    return existing;
                })
                .orElseGet(() -> OrganizationMember.builder()
                        .organization(organization)
                        .user(user)
                        .role(OrganizationRole.MEMBER)
                        .status(OrganizationMemberStatus.PENDING)
                        .build());

        return OrganizationMemberResponse.fromMembership(organizationMemberRepository.save(membership));
    }

    @Override
    public OrganizationMemberResponse approveMember(String adminPublicId, String organizationPublicId, String userPublicId) {
        ensureAdmin(adminPublicId, organizationPublicId);
        OrganizationMember membership = getMembershipOrThrow(organizationPublicId, userPublicId);
        membership.setStatus(OrganizationMemberStatus.APPROVED);
        membership.setJoinedAt(LocalDateTime.now());
        if (membership.getRole() == null) {
            membership.setRole(OrganizationRole.MEMBER);
        }
        return OrganizationMemberResponse.fromMembership(organizationMemberRepository.save(membership));
    }

    @Override
    public OrganizationMemberResponse rejectMember(String adminPublicId, String organizationPublicId, String userPublicId) {
        ensureAdmin(adminPublicId, organizationPublicId);
        OrganizationMember membership = getMembershipOrThrow(organizationPublicId, userPublicId);
        if (membership.getRole() == OrganizationRole.ADMIN) {
            throw new ForbiddenException("Cannot reject an organization admin");
        }
        membership.setStatus(OrganizationMemberStatus.REJECTED);
        membership.setJoinedAt(null);
        return OrganizationMemberResponse.fromMembership(organizationMemberRepository.save(membership));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberResponse> getApprovedMembers(String adminPublicId, String organizationPublicId) {
        ensureApprovedMember(adminPublicId, organizationPublicId);
        Organization organization = getOrganizationOrThrow(organizationPublicId);
        return organizationMemberRepository
                .findByOrganizationIdAndStatusOrderByUserUsernameAsc(
                        organization.getId(),
                        OrganizationMemberStatus.APPROVED
                )
                .stream()
                .map(OrganizationMemberResponse::fromMembership)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberResponse> getAllMembers(String adminPublicId, String organizationPublicId) {
        ensureAdmin(adminPublicId, organizationPublicId);
        return organizationMemberRepository.findByOrganizationPublicIdOrderByCreatedAtAsc(organizationPublicId)
                .stream()
                .map(OrganizationMemberResponse::fromMembership)
                .toList();
    }

    private User getUserOrThrow(String userPublicId) {
        return userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userPublicId));
    }

    private Organization getOrganizationOrThrow(String organizationPublicId) {
        return organizationRepository.findByPublicId(organizationPublicId)
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .orElseThrow(() -> new NotFoundException("Organization not found: " + organizationPublicId));
    }

    private OrganizationMember getMembershipOrThrow(String organizationPublicId, String userPublicId) {
        return organizationMemberRepository.findByOrganizationPublicIdAndUserPublicId(organizationPublicId, userPublicId)
                .orElseThrow(() -> new NotFoundException("Membership not found"));
    }

    private void ensureAdmin(String userPublicId, String organizationPublicId) {
        User user = getUserOrThrow(userPublicId);
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean admin = organizationMemberRepository.existsByOrganizationPublicIdAndUserPublicIdAndRoleAndStatus(
                organizationPublicId,
                userPublicId,
                OrganizationRole.ADMIN,
                OrganizationMemberStatus.APPROVED
        );
        if (!admin) {
            throw new ForbiddenException("Organization admin role is required");
        }
    }

    private void ensureApprovedMember(String userPublicId, String organizationPublicId) {
        User user = getUserOrThrow(userPublicId);
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean approved = organizationMemberRepository.existsByOrganizationPublicIdAndUserPublicIdAndStatus(
                organizationPublicId,
                userPublicId,
                OrganizationMemberStatus.APPROVED
        );
        if (!approved) {
            throw new ForbiddenException("Approved organization membership is required");
        }
    }

    private void ensureGlobalAdmin(User user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only admin users can create organizations");
        }
    }
}
