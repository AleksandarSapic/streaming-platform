package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.UserRoleDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.UserRole;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.repository.UserRoleRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {
    
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository,
                              UserRepository userRepository,
                              DtoMapper dtoMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public UserRoleDto createUserRole(UserRoleDto userRoleDto) {
        if (userRoleRepository.existsByName(userRoleDto.name())) {
            throw new RuntimeException("User role with name already exists: " + userRoleDto.name());
        }
        
        UserRole userRole = new UserRole();
        userRole.setName(userRoleDto.name());
        UserRole savedUserRole = userRoleRepository.save(userRole);
        return dtoMapper.toUserRoleDto(savedUserRole);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserRoleDto> getUserRoleById(UUID id) {
        return userRoleRepository.findById(id)
                .map(dtoMapper::toUserRoleDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserRoleDto> getUserRoleByName(String name) {
        return userRoleRepository.findByName(name)
                .map(dtoMapper::toUserRoleDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserRoleDto> getAllUserRoles() {
        List<UserRole> userRoles = userRoleRepository.findAll();
        return dtoMapper.toUserRoleDtoList(userRoles);
    }
    
    @Override
    public UserRoleDto updateUserRole(UserRoleDto userRoleDto) {
        UserRole existingUserRole = userRoleRepository.findById(userRoleDto.id())
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        // Check if new name conflicts with existing role (excluding current role)
        if (!existingUserRole.getName().equals(userRoleDto.name()) && 
            userRoleRepository.existsByName(userRoleDto.name())) {
            throw new RuntimeException("User role with name already exists: " + userRoleDto.name());
        }
        
        existingUserRole.setName(userRoleDto.name());
        UserRole savedUserRole = userRoleRepository.save(existingUserRole);
        return dtoMapper.toUserRoleDto(savedUserRole);
    }
    
    @Override
    public void deleteUserRole(UUID id) {
        if (!userRoleRepository.existsById(id)) {
            throw new RuntimeException("User role not found");
        }
        
        // Check if any users are assigned this role
        Long userCount = countUsersByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("Cannot delete user role. " + userCount + " users are assigned this role.");
        }
        
        userRoleRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return userRoleRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRoleId(UUID roleId) {
        List<User> users = userRepository.findByUserRoleName(
            userRoleRepository.findById(roleId)
                .map(UserRole::getName)
                .orElseThrow(() -> new RuntimeException("User role not found"))
        );
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countUsersByRoleId(UUID roleId) {
        String roleName = userRoleRepository.findById(roleId)
                .map(UserRole::getName)
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        return (long) userRepository.findByUserRoleName(roleName).size();
    }
}