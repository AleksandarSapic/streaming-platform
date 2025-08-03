package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;

import java.util.List;
import java.util.UUID;

public interface ContentGenreService {
    
    void addGenreToContent(UUID contentId, UUID genreId);
    
    void removeGenreFromContent(UUID contentId, UUID genreId);
    
    List<GenreDto> getGenresByContentId(UUID contentId);
    
    List<ContentDto> getContentByGenreId(UUID genreId);
    
    boolean isGenreAssignedToContent(UUID contentId, UUID genreId);
    
    Long getGenreCountByContentId(UUID contentId);
    
    Long getContentCountByGenreId(UUID genreId);
    
    void removeAllGenresFromContent(UUID contentId);
    
    void removeContentFromAllGenres(UUID genreId);
    
    List<ContentDto> searchContentByGenreName(String genreName);
    
    void bulkAssignGenresToContent(UUID contentId, List<UUID> genreIds);
}