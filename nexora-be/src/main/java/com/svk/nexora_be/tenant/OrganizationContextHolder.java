package com.svk.nexora_be.tenant;

public final class OrganizationContextHolder {
    private static final ThreadLocal<Long> ORGANIZATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ORGANIZATION_PUBLIC_ID = new ThreadLocal<>();

    private OrganizationContextHolder() {
    }

    public static void setOrganization(Long organizationId, String organizationPublicId) {
        ORGANIZATION_ID.set(organizationId);
        ORGANIZATION_PUBLIC_ID.set(organizationPublicId);
    }

    public static Long getOrganizationId() {
        return ORGANIZATION_ID.get();
    }

    public static String getOrganizationPublicId() {
        return ORGANIZATION_PUBLIC_ID.get();
    }

    public static Long requireOrganizationId() {
        Long organizationId = getOrganizationId();
        if (organizationId == null) {
            throw new IllegalStateException("Active organization context is required");
        }
        return organizationId;
    }

    public static String requireOrganizationPublicId() {
        String organizationPublicId = getOrganizationPublicId();
        if (organizationPublicId == null || organizationPublicId.isBlank()) {
            throw new IllegalStateException("Active organization context is required");
        }
        return organizationPublicId;
    }

    public static void clear() {
        ORGANIZATION_ID.remove();
        ORGANIZATION_PUBLIC_ID.remove();
    }
}
