package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.dto.request.SignupRequest;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

//    @Override
//    public AuthResponse login(LoginRequest request) throws Exception {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new Exception("User not found"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new Exception("Invalid password");
//        }
//
//        String token = jwtUtil.generateToken(user.getPublicId(), user.getEmail(), user.getRole().getAuthority());
//        UserResponse userResponse = UserResponse.fromUser(user);
//
//        return AuthResponse.builder()
//                .token(token)
//                .user(userResponse)
//                .build();
//    }

    @Override
    public void signup(SignupRequest request) throws Exception {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getUsername())
                .isActive(true)
                .build();

        userRepository.save(user);
    }
}
