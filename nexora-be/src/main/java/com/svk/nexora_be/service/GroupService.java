package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.GroupRequest;
import com.svk.nexora_be.dto.response.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request, String userId);
    GroupResponse getGroupById(String groupId);
    List<GroupResponse> getAllGroups();
    void addMemberToGroup(String groupId, String userId);
    void removeMemberFromGroup(String groupId, String userId);
}
