package com.svk.nexora_be.service;

import com.svk.nexora_be.dto.request.LoginRequest;
import com.svk.nexora_be.dto.request.SignupRequest;

public interface AuthService {
//    AuthResponse login(LoginRequest request) throws Exception;
    void signup(SignupRequest request) throws Exception;
}
