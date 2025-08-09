package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.AuthResponse;
import com.aleksandar.streaming_platform.backend.dto.LoginRequest;
import com.aleksandar.streaming_platform.backend.dto.RegisterRequest;

public interface AuthService {
    
    AuthResponse login(LoginRequest loginRequest);
    
    AuthResponse register(RegisterRequest registerRequest);
    
    boolean existsByEmail(String email);
}