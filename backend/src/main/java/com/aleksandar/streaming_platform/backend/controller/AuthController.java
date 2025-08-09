package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.AuthResponse;
import com.aleksandar.streaming_platform.backend.dto.LoginRequest;
import com.aleksandar.streaming_platform.backend.dto.RegisterRequest;
import com.aleksandar.streaming_platform.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = authService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}