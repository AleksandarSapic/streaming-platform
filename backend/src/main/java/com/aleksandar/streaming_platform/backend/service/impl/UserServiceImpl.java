package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.CreateUserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRole;
import com.aleksandar.streaming_platform.backend.model.Watchlist;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.WatchlistId;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRoleRepository;
import com.aleksandar.streaming_platform.backend.repository.WatchlistRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    
    public UserServiceImpl(UserRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          WatchlistRepository watchlistRepository,
                          ContentRepository contentRepository,
                          DtoMapper dtoMapper) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.watchlistRepository = watchlistRepository;
        this.contentRepository = contentRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = dtoMapper.toUserEntity(createUserDto);
        // TODO: Hash password before saving
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
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByCountry(String country) {
        List<User> users = userRepository.findByCountry(country);
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRoleName(String roleName) {
        List<User> users = userRepository.findByUserRoleName(roleName);
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByName(String name) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCase(name);
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setFullName(userDto.fullName());
        existingUser.setEmail(userDto.email());
        existingUser.setCountry(userDto.country());
        
        User savedUser = userRepository.save(existingUser);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
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
        // TODO: Implement password verification
        return user.map(value -> value.getHashedPassword().equals(password)).orElse(false);
    }
    
    @Override
    public UserDto changeUserPassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // TODO: Verify old password and hash new password
        if (!user.getHashedPassword().equals(oldPassword)) {
            throw new RuntimeException("Invalid old password");
        }
        
        user.setHashedPassword(newPassword);
        User savedUser = userRepository.save(user);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    public UserDto assignUserRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserRole userRole = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("User role not found"));
        
        user.setUserRole(userRole);
        User savedUser = userRepository.save(user);
        return dtoMapper.toUserDto(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDto> getWatchlistByUserId(UUID userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        return dtoMapper.toWatchlistDtoList(watchlists);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getRecommendedContentForUser(UUID userId) {
        // TODO: Implement recommendation algorithm
        // For now, return recent available content
        List<Content> recentContent = contentRepository.findAvailableContentOrderByReleaseDateDesc();
        return dtoMapper.toContentDtoList(recentContent);
    }
    
    @Override
    public void addToWatchlist(UUID userId, UUID contentId) {
        if (watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new RuntimeException("Content already in watchlist");
        }
        
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setContentId(contentId);
        watchlistRepository.save(watchlist);
    }
    
    @Override
    public void removeFromWatchlist(UUID userId, UUID contentId) {
        if (!watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new RuntimeException("Content not in watchlist");
        }
        watchlistRepository.deleteByUserIdAndContentId(userId, contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentInUserWatchlist(UUID userId, UUID contentId) {
        return watchlistRepository.existsByUserIdAndContentId(userId, contentId);
    }
}