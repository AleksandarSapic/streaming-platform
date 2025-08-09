package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.UserRoleDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.DuplicateResourceException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.UserRole;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
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
        UserRoleType roleType = UserRoleType.fromString(userRoleDto.name());
        
        if (userRoleRepository.existsByName(roleType)) {
            throw new DuplicateResourceException("UserRole", "name", userRoleDto.name());
        }
        
        UserRole userRole = new UserRole();
        userRole.setName(roleType);
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
        try {
            UserRoleType roleType = UserRoleType.fromString(name);
            return userRoleRepository.findByName(roleType)
                    .map(dtoMapper::toUserRoleDto);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
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
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "id", userRoleDto.id()));
        
        UserRoleType newRoleType = UserRoleType.fromString(userRoleDto.name());
        
        // Check if new name conflicts with existing role (excluding current role)
        if (!existingUserRole.getName().equals(newRoleType) && 
            userRoleRepository.existsByName(newRoleType)) {
            throw new DuplicateResourceException("UserRole", "name", userRoleDto.name());
        }
        
        existingUserRole.setName(newRoleType);
        UserRole savedUserRole = userRoleRepository.save(existingUserRole);
        return dtoMapper.toUserRoleDto(savedUserRole);
    }
    
    @Override
    public void deleteUserRole(UUID id) {
        if (!userRoleRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserRole", "id", id);
        }
        
        // Check if any users are assigned this role
        Long userCount = countUsersByRoleId(id);
        if (userCount > 0) {
            throw new BusinessLogicException("Cannot delete user role. " + userCount + " users are assigned this role.");
        }
        
        userRoleRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        try {
            UserRoleType roleType = UserRoleType.fromString(name);
            return userRoleRepository.existsByName(roleType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRoleId(UUID roleId) {
        UserRoleType roleType = userRoleRepository.findById(roleId)
                .map(UserRole::getName)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "id", roleId));
        
        List<User> users = userRepository.findByUserRoleType(roleType);
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countUsersByRoleId(UUID roleId) {
        UserRoleType roleType = userRoleRepository.findById(roleId)
                .map(UserRole::getName)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "id", roleId));
        
        return (long) userRepository.findByUserRoleType(roleType).size();
    }
}