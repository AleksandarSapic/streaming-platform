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

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        List<UserDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-country")
    public ResponseEntity<List<UserDto>> getUsersByCountry(@RequestParam String country) {
        List<UserDto> users = userService.getUsersByCountry(country);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-role")
    public ResponseEntity<List<UserDto>> getUsersByRole(@RequestParam String roleName) {
        List<UserDto> users = userService.getUsersByRoleName(roleName);
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
    public ResponseEntity<List<WatchlistDto>> getUserWatchlist(@PathVariable UUID id) {
        authorizationService.validateWatchlistAccess(id);
        List<WatchlistDto> watchlist = userService.getWatchlistByUserId(id);
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
    public ResponseEntity<List<ContentDto>> getRecommendations(@PathVariable UUID id) {
        authorizationService.validateUserAccess(id);
        List<ContentDto> recommendations = userService.getRecommendedContentForUser(id);
        return ResponseEntity.ok(recommendations);
    }
    
    @GetMapping("/{id}/watchlist/check")
    public ResponseEntity<Boolean> isContentInWatchlist(@PathVariable UUID id, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(id);
        boolean isInWatchlist = userService.isContentInUserWatchlist(id, contentId);
        return ResponseEntity.ok(isInWatchlist);
    }
}