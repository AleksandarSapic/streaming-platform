package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateUserDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.exception.AuthenticationException;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.DuplicateResourceException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.*;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRoleRepository;
import com.aleksandar.streaming_platform.backend.repository.WatchlistRepository;
import com.aleksandar.streaming_platform.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final WatchlistRepository watchlistRepository;
    private final ContentRepository contentRepository;
    private final DtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          WatchlistRepository watchlistRepository,
                          ContentRepository contentRepository,
                          DtoMapper dtoMapper,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.watchlistRepository = watchlistRepository;
        this.contentRepository = contentRepository;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new DuplicateResourceException("User", "email", createUserDto.email());
        }
        
        User user = dtoMapper.toUserEntity(createUserDto);
        user.setHashedPassword(passwordEncoder.encode(createUserDto.password()));
        
        if (user.getUserRole() == null) {
            UserRole defaultRole = userRoleRepository.findByName(UserRoleType.USER)
                    .orElseGet(() -> {
                        UserRole newRole = new UserRole();
                        newRole.setName(UserRoleType.USER);
                        return userRoleRepository.save(newRole);
                    });
            user.setUserRole(defaultRole);
        }
        
        User savedUser = userRepository.save(user);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(dtoMapper::toUserDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(dtoMapper::toUserDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(dtoMapper::toUserDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByCountry(String country, Pageable pageable) {
        Page<User> users = userRepository.findByCountry(country, pageable);
        return users.map(dtoMapper::toUserDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByRoleName(String roleName, Pageable pageable) {
        Page<User> users = userRepository.findByUserRoleName(roleName, pageable);
        return users.map(dtoMapper::toUserDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsersByName(String name, Pageable pageable) {
        Page<User> users = userRepository.findByFullNameContainingIgnoreCase(name, pageable);
        return users.map(dtoMapper::toUserDto);
    }
    
    @Override
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDto.id()));
        
        existingUser.setFullName(userDto.fullName());
        existingUser.setEmail(userDto.email());
        existingUser.setCountry(userDto.country());
        
        User savedUser = userRepository.save(existingUser);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateUserCredentials(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(value -> passwordEncoder.matches(password, value.getHashedPassword())).orElse(false);
    }
    
    @Override
    public UserDto changeUserPassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            throw new AuthenticationException("Invalid old password");
        }
        
        user.setHashedPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    public UserDto assignUserRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        UserRole userRole = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "id", roleId));
        
        user.setUserRole(userRole);
        User savedUser = userRepository.save(user);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<WatchlistDto> getWatchlistByUserId(UUID userId, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId, pageable);
        return watchlists.map(dtoMapper::toWatchlistDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getRecommendedContentForUser(UUID userId, Pageable pageable) {
        // TODO: Implement recommendation algorithm
        // For now, return recent available content
        Page<Content> recentContent = contentRepository.findAvailableContentOrderByReleaseDateDesc(pageable);
        return recentContent.map(dtoMapper::toContentDto);
    }
    
    @Override
    public void addToWatchlist(UUID userId, UUID contentId) {
        if (watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new BusinessLogicException("Content already in watchlist");
        }
        
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setContentId(contentId);
        watchlistRepository.save(watchlist);
    }
    
    @Override
    public void removeFromWatchlist(UUID userId, UUID contentId) {
        if (!watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new BusinessLogicException("Content not in watchlist");
        }
        watchlistRepository.deleteByUserIdAndContentId(userId, contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentInUserWatchlist(UUID userId, UUID contentId) {
        return watchlistRepository.existsByUserIdAndContentId(userId, contentId);
    }
}