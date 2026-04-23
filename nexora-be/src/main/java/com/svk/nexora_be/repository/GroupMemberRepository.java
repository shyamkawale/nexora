package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.Group;
import com.svk.nexora_be.entity.GroupMember;
import com.svk.nexora_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    List<GroupMember> findByGroupAndIsActiveTrue(Group group);
    List<GroupMember> findByUserAndIsActiveTrue(User user);
    boolean existsByGroupAndUser(Group group, User user);
}
