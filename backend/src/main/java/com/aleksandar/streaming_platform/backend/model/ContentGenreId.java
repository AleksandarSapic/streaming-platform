package com.aleksandar.streaming_platform.backend.model;

import java.io.Serializable;
import java.util.UUID;

public record ContentGenreId(UUID contentId, UUID genreId) implements Serializable {
}