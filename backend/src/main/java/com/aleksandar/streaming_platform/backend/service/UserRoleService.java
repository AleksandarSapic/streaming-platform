package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.UserRoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleService {
    
    UserRoleDto createUserRole(UserRoleDto userRoleDto);
    
    Optional<UserRoleDto> getUserRoleById(UUID id);
    
    Optional<UserRoleDto> getUserRoleByName(String name);
    
    Page<UserRoleDto> getAllUserRoles(Pageable pageable);
    
    UserRoleDto updateUserRole(UserRoleDto userRoleDto);
    
    void deleteUserRole(UUID id);
    
    boolean existsByName(String name);
    
    Page<UserDto> getUsersByRoleId(UUID roleId, Pageable pageable);
    
    Long countUsersByRoleId(UUID roleId);
}