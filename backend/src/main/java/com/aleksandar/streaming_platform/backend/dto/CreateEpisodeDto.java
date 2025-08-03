package com.aleksandar.streaming_platform.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEpisodeDto(
    @NotNull(message = "Season number is required")
    @Positive(message = "Season number must be positive")
    Integer seasonNumber,
    
    @NotNull(message = "Episode number is required")
    @Positive(message = "Episode number must be positive")
    Integer episodeNumber,
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    String title,
    
    String description,
    String duration,
    LocalDate releaseDate,
    String thumbnailUrl,
    String videoUrl,
    
    @NotNull(message = "Content ID is required")
    UUID contentId
) {}