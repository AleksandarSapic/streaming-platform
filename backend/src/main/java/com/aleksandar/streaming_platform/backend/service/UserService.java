package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.CreateUserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    
    UserDto createUser(CreateUserDto createUserDto);
    
    Optional<UserDto> getUserById(UUID id);
    
    Optional<UserDto> getUserByEmail(String email);
    
    List<UserDto> getAllUsers();
    
    List<UserDto> getUsersByCountry(String country);
    
    List<UserDto> getUsersByRoleName(String roleName);
    
    List<UserDto> searchUsersByName(String name);
    
    UserDto updateUser(UserDto userDto);
    
    void deleteUser(UUID id);
    
    boolean existsByEmail(String email);
    
    boolean validateUserCredentials(String email, String password);
    
    UserDto changeUserPassword(UUID userId, String oldPassword, String newPassword);
    
    UserDto assignUserRole(UUID userId, UUID roleId);
    
    List<WatchlistDto> getWatchlistByUserId(UUID userId);
    
    List<ContentDto> getRecommendedContentForUser(UUID userId);
    
    void addToWatchlist(UUID userId, UUID contentId);
    
    void removeFromWatchlist(UUID userId, UUID contentId);
    
    boolean isContentInUserWatchlist(UUID userId, UUID contentId);
}