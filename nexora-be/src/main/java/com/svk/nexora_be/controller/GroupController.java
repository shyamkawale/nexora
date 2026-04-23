package com.svk.nexora_be.controller;

import com.svk.nexora_be.dto.request.GroupRequest;
import com.svk.nexora_be.dto.response.GroupResponse;
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
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupRequest request) {
        String userId = jwtUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        GroupResponse group = groupService.createGroup(request, userId);
        if (group != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(group);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable String groupId) {
        GroupResponse group = groupService.getGroupById(groupId);
        if (group != null) {
            return ResponseEntity.ok(group);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMember(@PathVariable String groupId, @PathVariable String userId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable String groupId, @PathVariable String userId) {
        String currentUserId = jwtUtil.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }

        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }
}
