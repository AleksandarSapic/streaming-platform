package com.aleksandar.streaming_platform.backend.model;

import java.io.Serializable;
import java.util.UUID;

public record WatchlistId(UUID userId, UUID contentId) implements Serializable {
}