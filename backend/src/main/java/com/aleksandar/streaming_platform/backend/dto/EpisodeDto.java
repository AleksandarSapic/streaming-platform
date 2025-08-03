package com.aleksandar.streaming_platform.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EpisodeDto(
    UUID id,
    Integer seasonNumber,
    Integer episodeNumber,
    String title,
    String description,
    String duration,
    LocalDate releaseDate,
    String thumbnailUrl,
    String videoUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UUID contentId,
    String contentTitle
) {}