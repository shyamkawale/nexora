package com.svk.nexora_be.tenant;

import com.svk.nexora_be.entity.Organization;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.enums.UserRole;
import com.svk.nexora_be.repository.OrganizationMemberRepository;
import com.svk.nexora_be.repository.OrganizationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OrganizationInterceptor implements HandlerInterceptor {
    public static final String ORGANIZATION_HEADER = "X-Organization-Id";

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String organizationPublicId = request.getHeader(ORGANIZATION_HEADER);

        if (organizationPublicId == null || organizationPublicId.isBlank()) {
            if (requiresTenant(path)) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), ORGANIZATION_HEADER + " header is required");
                return false;
            }
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication is required");
            return false;
        }

        User currentUser = (User) authentication.getPrincipal();
        String userPublicId = currentUser.getPublicId();
        Organization organization = organizationRepository.findByPublicId(organizationPublicId)
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .orElse(null);
        if (organization == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Organization not found");
            return false;
        }

        boolean approvedMember = currentUser.getRole() == UserRole.ADMIN || organizationMemberRepository
                .existsByOrganizationPublicIdAndUserPublicIdAndStatus(
                        organizationPublicId,
                        userPublicId,
                        OrganizationMemberStatus.APPROVED
                );
        if (!approvedMember) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "User is not an approved member of this organization");
            return false;
        }

        OrganizationContextHolder.setOrganization(organization.getId(), organization.getPublicId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        OrganizationContextHolder.clear();
    }

    private boolean requiresTenant(String path) {
        return path.startsWith("/api/v1/posts")
                || path.startsWith("/api/v1/post-comments")
                || path.startsWith("/api/v1/likes")
                || path.startsWith("/api/v1/files")
                || path.startsWith("/api/v1/direct-chats")
                || path.startsWith("/api/v1/group-chats")
                || path.startsWith("/api/v1/presence")
                || path.startsWith("/api/v1/user/presence")
                || path.startsWith("/api/v1/users");
    }
}
