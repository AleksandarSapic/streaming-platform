package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.AuthResponse;
import com.aleksandar.streaming_platform.backend.dto.LoginRequest;
import com.aleksandar.streaming_platform.backend.dto.RegisterRequest;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.exception.AuthenticationException;
import com.aleksandar.streaming_platform.backend.exception.DuplicateResourceException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRole;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRoleRepository;
import com.aleksandar.streaming_platform.backend.security.JwtTokenService;
import com.aleksandar.streaming_platform.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final DtoMapper dtoMapper;

    public AuthServiceImpl(UserRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenService jwtTokenService,
                          DtoMapper dtoMapper) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getHashedPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        String token = jwtTokenService.generateToken(user.getId());
        
        UserDto userDto = dtoMapper.toUserDto(user);
        return new AuthResponse(token, userDto);
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new DuplicateResourceException("User", "email", registerRequest.email());
        }

        UserRole userRole = userRoleRepository.findByName(UserRoleType.USER)
                .orElseGet(() -> {
                    UserRole newRole = new UserRole();
                    newRole.setName(UserRoleType.USER);
                    return userRoleRepository.save(newRole);
                });

        User user = new User();
        user.setFullName(registerRequest.fullName());
        user.setEmail(registerRequest.email());
        user.setHashedPassword(passwordEncoder.encode(registerRequest.password()));
        user.setCountry(registerRequest.country());
        user.setUserRole(userRole);

        User savedUser = userRepository.save(user);
        
        String token = jwtTokenService.generateToken(savedUser.getId());
        UserDto userDto = dtoMapper.toUserDto(savedUser);
        
        return new AuthResponse(token, userDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}