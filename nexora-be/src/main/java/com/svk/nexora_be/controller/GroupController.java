package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.GroupRequest;
import com.svk.nexora_be.dto.response.GroupResponse;
import com.svk.nexora_be.model.ApiResponse;
import com.svk.nexora_be.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.svk.nexora_be.security.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@RequestBody GroupRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        GroupResponse group = groupService.createGroup(request, userId);
        if (group != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(group, "Group created"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Failed to create group"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success(groups, "Groups fetched"));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(@PathVariable String groupId) {
        GroupResponse group = groupService.getGroupById(groupId);
        if (group != null) {
            return ResponseEntity.ok(ApiResponse.success(group, "Group fetched"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Group not found"));
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> addMember(@PathVariable String groupId, @PathVariable String userId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member added"));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable String groupId, @PathVariable String userId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"));
        }

        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed"));
    }
}
