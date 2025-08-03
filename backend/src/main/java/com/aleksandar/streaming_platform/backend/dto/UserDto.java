package com.aleksandar.streaming_platform.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String fullName,
    String email,
    String country,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserRoleDto userRole
) {}