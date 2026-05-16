package com.svk.nexora_be.repository;

import com.svk.nexora_be.entity.GroupChat;
import com.svk.nexora_be.entity.GroupChatMember;
import com.svk.nexora_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupChatMemberRepository extends JpaRepository<GroupChatMember, Long> {
    Optional<GroupChatMember> findByGroupChatAndUser(GroupChat groupChat, User user);
    List<GroupChatMember> findByGroupChatAndIsActiveTrue(GroupChat groupChat);
    List<GroupChatMember> findByUserAndIsActiveTrue(User user);
    boolean existsByGroupChatAndUser(GroupChat groupChat, User user);
    List<GroupChatMember> findByGroupChatPublicId(String groupChatPublicId);

    @Query("SELECT gcm FROM GroupChatMember gcm WHERE gcm.groupChat.id = :groupChatId AND gcm.user.id = :userId")
    Optional<GroupChatMember> findByGroupChatIdAndUserId(@Param("groupChatId") Long groupChatId, @Param("userId") Long userId);
}
