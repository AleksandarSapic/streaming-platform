package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.CreateUserDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.security.AuthorizationService;
import com.aleksandar.streaming_platform.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserService userService;
    private final AuthorizationService authorizationService;
    
    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto user = userService.createUser(createUserDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        authorizationService.validateUserAccess(id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(@RequestParam String name, Pageable pageable) {
        Page<UserDto> users = userService.searchUsersByName(name, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-country")
    public ResponseEntity<Page<UserDto>> getUsersByCountry(@RequestParam String country, Pageable pageable) {
        Page<UserDto> users = userService.getUsersByCountry(country, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-role")
    public ResponseEntity<Page<UserDto>> getUsersByRole(@RequestParam String roleName, Pageable pageable) {
        Page<UserDto> users = userService.getUsersByRoleName(roleName, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @Valid @RequestBody UserDto userDto) {
        if (!id.equals(userDto.id())) {
            return ResponseEntity.badRequest().build();
        }

        authorizationService.validateUserAccess(id);
        UserDto updatedUser = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/assign-role")
    public ResponseEntity<UserDto> assignRole(@PathVariable UUID id, @RequestParam UUID roleId) {
        authorizationService.validateRoleAssignmentAccess();
        UserDto user = userService.assignUserRole(id, roleId);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}/watchlist")
    public ResponseEntity<Page<WatchlistDto>> getUserWatchlist(@PathVariable UUID id, Pageable pageable) {
        authorizationService.validateWatchlistAccess(id);
        Page<WatchlistDto> watchlist = userService.getWatchlistByUserId(id, pageable);
        return ResponseEntity.ok(watchlist);
    }
    
    @PostMapping("/{id}/watchlist")
    public ResponseEntity<Void> addToWatchlist(@PathVariable UUID id, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(id);
        userService.addToWatchlist(id, contentId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}/watchlist/{contentId}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable UUID id, @PathVariable UUID contentId) {
        authorizationService.validateWatchlistAccess(id);
        userService.removeFromWatchlist(id, contentId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<Page<ContentDto>> getRecommendations(@PathVariable UUID id, Pageable pageable) {
        authorizationService.validateUserAccess(id);
        Page<ContentDto> recommendations = userService.getRecommendedContentForUser(id, pageable);
        return ResponseEntity.ok(recommendations);
    }
    
    @GetMapping("/{id}/watchlist/check")
    public ResponseEntity<Boolean> isContentInWatchlist(@PathVariable UUID id, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(id);
        boolean isInWatchlist = userService.isContentInUserWatchlist(id, contentId);
        return ResponseEntity.ok(isInWatchlist);
    }
}