package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    User getUserByPublicId(String publicId);
    User getUserByEmail(String email);
    UserResponse getUserInfo(String userId);
    List<UserResponse> getAllUsers(Pageable pageable);
    List<UserResponse> searchUsers(String query);
    User updateUser(User user);
    void deleteUser(String publicId);
}
