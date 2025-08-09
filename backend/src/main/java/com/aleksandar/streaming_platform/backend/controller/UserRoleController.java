package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.UserRoleDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.service.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/user-roles")
public class UserRoleController {
    
    private final UserRoleService userRoleService;
    
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }
    
    @PostMapping
    public ResponseEntity<UserRoleDto> createUserRole(@Valid @RequestBody UserRoleDto userRoleDto) {
        UserRoleDto userRole = userRoleService.createUserRole(userRoleDto);
        return new ResponseEntity<>(userRole, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDto> getUserRoleById(@PathVariable UUID id) {
        return userRoleService.getUserRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<UserRoleDto> getUserRoleByName(@PathVariable String name) {
        return userRoleService.getUserRoleByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<UserRoleDto>> getAllUserRoles(Pageable pageable) {
        Page<UserRoleDto> userRoles = userRoleService.getAllUserRoles(pageable);
        return ResponseEntity.ok(userRoles);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserRoleDto> updateUserRole(@PathVariable UUID id, @Valid @RequestBody UserRoleDto userRoleDto) {
        if (!id.equals(userRoleDto.id())) {
            return ResponseEntity.badRequest().build();
        }
        UserRoleDto updatedUserRole = userRoleService.updateUserRole(userRoleDto);
        return ResponseEntity.ok(updatedUserRole);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable UUID id) {
        userRoleService.deleteUserRole(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/users")
    public ResponseEntity<Page<UserDto>> getUsersByRole(@PathVariable UUID id, Pageable pageable) {
        Page<UserDto> users = userRoleService.getUsersByRoleId(id, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}/users/count")
    public ResponseEntity<Long> getUserCountByRole(@PathVariable UUID id) {
        Long count = userRoleService.countUsersByRoleId(id);
        return ResponseEntity.ok(count);
    }
}