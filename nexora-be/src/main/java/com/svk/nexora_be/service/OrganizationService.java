package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.CreateOrganizationRequest;
import com.svk.nexora_be.dto.response.OrganizationMemberResponse;
import com.svk.nexora_be.dto.response.OrganizationResponse;

import java.util.List;

public interface OrganizationService {
    OrganizationResponse createOrganization(String creatorPublicId, CreateOrganizationRequest request);

    List<OrganizationResponse> getMyOrganizations(String userPublicId);

    List<OrganizationResponse> getActiveOrganizations();

    OrganizationMemberResponse requestToJoin(String userPublicId, String organizationPublicId);

    OrganizationMemberResponse approveMember(String adminPublicId, String organizationPublicId, String userPublicId);

    OrganizationMemberResponse rejectMember(String adminPublicId, String organizationPublicId, String userPublicId);

    List<OrganizationMemberResponse> getApprovedMembers(String adminPublicId, String organizationPublicId);

    List<OrganizationMemberResponse> getAllMembers(String adminPublicId, String organizationPublicId);
}
