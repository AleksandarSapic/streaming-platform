package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateUserDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    
    UserDto createUser(CreateUserDto createUserDto);
    
    Optional<UserDto> getUserById(UUID id);
    
    Optional<UserDto> getUserByEmail(String email);
    
    Page<UserDto> getAllUsers(Pageable pageable);
    
    Page<UserDto> getUsersByCountry(String country, Pageable pageable);
    
    Page<UserDto> getUsersByRoleName(String roleName, Pageable pageable);
    
    Page<UserDto> searchUsersByName(String name, Pageable pageable);
    
    UserDto updateUser(UserDto userDto);
    
    void deleteUser(UUID id);
    
    boolean existsByEmail(String email);
    
    boolean validateUserCredentials(String email, String password);
    
    UserDto changeUserPassword(UUID userId, String oldPassword, String newPassword);
    
    UserDto assignUserRole(UUID userId, UUID roleId);
    
    Page<WatchlistDto> getWatchlistByUserId(UUID userId, Pageable pageable);
    
    Page<ContentDto> getRecommendedContentForUser(UUID userId, Pageable pageable);
    
    void addToWatchlist(UUID userId, UUID contentId);
    
    void removeFromWatchlist(UUID userId, UUID contentId);
    
    boolean isContentInUserWatchlist(UUID userId, UUID contentId);
}