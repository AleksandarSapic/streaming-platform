package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.UserRoleDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleService {
    
    UserRoleDto createUserRole(UserRoleDto userRoleDto);
    
    Optional<UserRoleDto> getUserRoleById(UUID id);
    
    Optional<UserRoleDto> getUserRoleByName(String name);
    
    List<UserRoleDto> getAllUserRoles();
    
    UserRoleDto updateUserRole(UserRoleDto userRoleDto);
    
    void deleteUserRole(UUID id);
    
    boolean existsByName(String name);
    
    List<UserDto> getUsersByRoleId(UUID roleId);
    
    Long countUsersByRoleId(UUID roleId);
}