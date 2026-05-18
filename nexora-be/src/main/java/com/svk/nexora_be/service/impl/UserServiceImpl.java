package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.response.UserResponse;
import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.repository.OrganizationMemberRepository;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.UserService;
import com.svk.nexora_be.tenant.OrganizationContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserResponse getUserInfo(String userId) {
        User user = getUserByPublicId(userId);
        if (user != null) {
            return UserResponse.fromUser(user);
        }
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers(Pageable pageable) {
        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId != null) {
            return organizationMemberRepository
                    .findByOrganizationIdAndStatusOrderByUserUsernameAsc(
                            organizationId,
                            OrganizationMemberStatus.APPROVED
                    )
                    .stream()
                    .map(membership -> UserResponse.fromUser(membership.getUser()))
                    .collect(Collectors.toList());
        }

        return userRepository.findAll(pageable)
                .stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> searchUsers(String query) {
        String searchQuery = query.toLowerCase();

        Long organizationId = OrganizationContextHolder.getOrganizationId();
        if (organizationId != null) {
            return organizationMemberRepository
                    .findByOrganizationIdAndStatusOrderByUserUsernameAsc(
                            organizationId,
                            OrganizationMemberStatus.APPROVED
                    )
                    .stream()
                    .map(membership -> membership.getUser())
                    .filter(user ->
                            (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchQuery)) ||
                            user.getEmail().toLowerCase().contains(searchQuery)
                    )
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
        }

        return userRepository.findAll()
                .stream()
                .filter(user -> 
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchQuery)) ||
                    user.getEmail().toLowerCase().contains(searchQuery)
                )
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String publicId) {
        User user = getUserByPublicId(publicId);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByPublicId(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
