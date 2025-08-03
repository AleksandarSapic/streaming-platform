package com.aleksandar.streaming_platform.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ContentDto(
    UUID id,
    String title,
    String description,
    LocalDate releaseDate,
    String duration,
    String language,
    String thumbnailUrl,
    String videoUrl,
    boolean isAvailable,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    ContentTypeDto contentType,
    List<GenreDto> genres,
    Integer episodeCount
) {}