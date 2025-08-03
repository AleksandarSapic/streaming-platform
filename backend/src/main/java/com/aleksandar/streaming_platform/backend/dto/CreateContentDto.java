package com.aleksandar.streaming_platform.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateContentDto(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,
    
    String description,
    LocalDate releaseDate,
    String duration,
    String language,
    String thumbnailUrl,
    String videoUrl,
    boolean isAvailable,
    
    @NotNull(message = "Content type is required")
    UUID contentTypeId,
    
    List<UUID> genreIds
) {}