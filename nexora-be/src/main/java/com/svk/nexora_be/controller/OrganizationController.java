package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.CreateOrganizationRequest;
import com.svk.nexora_be.dto.response.OrganizationMemberResponse;
import com.svk.nexora_be.dto.response.OrganizationResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.security.JwtUtil;
import com.svk.nexora_be.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @RequestBody CreateOrganizationRequest request) {
        String userId = getCurrentUserOrThrow();
        OrganizationResponse response = organizationService.createOrganization(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Organization created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getOrganizations() {
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.getActiveOrganizations(),
                "Organizations fetched"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getMyOrganizations() {
        String userId = getCurrentUserOrThrow();
        return ResponseEntity.ok(ApiResponse.success(
                organizationService.getMyOrganizations(userId),
                "My organizations fetched"
        ));
    }

    @PostMapping("/{orgId}/join")
    public ResponseEntity<ApiResponse<OrganizationMemberResponse>> requestJoin(@PathVariable String orgId) {
        String userId = getCurrentUserOrThrow();
        OrganizationMemberResponse response = organizationService.requestToJoin(userId, orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Join request submitted"));
    }

    @PutMapping("/{orgId}/members/{userId}/approve")
    public ResponseEntity<ApiResponse<OrganizationMemberResponse>> approveMember(
            @PathVariable String orgId,
            @PathVariable String userId) {
        String adminId = getCurrentUserOrThrow();
        OrganizationMemberResponse response = organizationService.approveMember(adminId, orgId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member approved"));
    }

    @PutMapping("/{orgId}/members/{userId}/reject")
    public ResponseEntity<ApiResponse<OrganizationMemberResponse>> rejectMember(
            @PathVariable String orgId,
            @PathVariable String userId) {
        String adminId = getCurrentUserOrThrow();
        OrganizationMemberResponse response = organizationService.rejectMember(adminId, orgId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Member rejected"));
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<ApiResponse<List<OrganizationMemberResponse>>> getMembers(
            @PathVariable String orgId,
            @RequestParam(defaultValue = "false") boolean includePending) {
        String userId = getCurrentUserOrThrow();
        List<OrganizationMemberResponse> response = includePending
                ? organizationService.getAllMembers(userId, orgId)
                : organizationService.getApprovedMembers(userId, orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "Organization members fetched"));
    }

    private String getCurrentUserOrThrow() {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userId;
    }
}
