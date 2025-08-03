package com.aleksandar.streaming_platform.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WatchlistDto(
    UUID userId,
    UUID contentId,
    LocalDateTime addedAt,
    UserDto user,
    ContentDto content
) {}