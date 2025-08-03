package com.aleksandar.streaming_platform.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record GenreDto(
    UUID id,

    @NotBlank(message = "Genre name is required")
    @Size(min = 1, max = 100, message = "Genre name must be between 1 and 100 characters")
    String name
) {}