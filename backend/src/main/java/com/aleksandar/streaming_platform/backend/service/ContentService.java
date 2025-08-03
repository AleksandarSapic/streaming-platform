package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentService {
    
    ContentDto createContent(CreateContentDto createContentDto);
    
    Optional<ContentDto> getContentById(UUID id);
    
    List<ContentDto> getAllContent();
    
    List<ContentDto> getAvailableContent();
    
    List<ContentDto> getContentByType(String typeName);
    
    List<ContentDto> getContentByGenre(String genreName);
    
    List<ContentDto> searchContentByTitle(String title);
    
    List<ContentDto> getContentByLanguage(String language);
    
    List<ContentDto> getContentByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<ContentDto> getRecentContent();
    
    ContentDto updateContent(ContentDto contentDto);
    
    void deleteContent(UUID id);
    
    ContentDto toggleContentAvailability(UUID contentId);
    
    List<EpisodeDto> getEpisodesByContentId(UUID contentId);
    
    List<GenreDto> getGenresByContentId(UUID contentId);
    
    void addGenreToContent(UUID contentId, UUID genreId);
    
    void removeGenreFromContent(UUID contentId, UUID genreId);
    
    List<ContentDto> getPopularContent();
    
    List<ContentDto> getContentRecommendations(UUID userId);
}