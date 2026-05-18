package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Organization;
import com.svk.nexora_be.entity.OrganizationMember;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    Optional<OrganizationMember> findByOrganizationAndUser(Organization organization, User user);

    Optional<OrganizationMember> findByOrganizationPublicIdAndUserPublicId(String organizationPublicId, String userPublicId);

    boolean existsByOrganizationPublicIdAndUserPublicIdAndStatus(
            String organizationPublicId,
            String userPublicId,
            OrganizationMemberStatus status
    );

    boolean existsByOrganizationIdAndUserPublicIdAndStatus(
            Long organizationId,
            String userPublicId,
            OrganizationMemberStatus status
    );

    boolean existsByOrganizationPublicIdAndUserPublicIdAndRoleAndStatus(
            String organizationPublicId,
            String userPublicId,
            OrganizationRole role,
            OrganizationMemberStatus status
    );

    List<OrganizationMember> findByUserPublicIdOrderByCreatedAtDesc(String userPublicId);

    List<OrganizationMember> findByOrganizationPublicIdOrderByCreatedAtAsc(String organizationPublicId);

    List<OrganizationMember> findByOrganizationIdAndStatusOrderByUserUsernameAsc(
            Long organizationId,
            OrganizationMemberStatus status
    );
}
