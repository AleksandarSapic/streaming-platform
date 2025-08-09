package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ContentService {
    
    ContentDto createContent(CreateContentDto createContentDto);
    
    Optional<ContentDto> getContentById(UUID id);
    
    Page<ContentDto> getAllContent(Pageable pageable);
    
    Page<ContentDto> getAvailableContent(Pageable pageable);
    
    Page<ContentDto> getContentByType(String typeName, Pageable pageable);
    
    Page<ContentDto> getContentByGenre(String genreName, Pageable pageable);
    
    Page<ContentDto> searchContentByTitle(String title, Pageable pageable);
    
    Page<ContentDto> getContentByLanguage(String language, Pageable pageable);
    
    Page<ContentDto> getContentByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Page<ContentDto> getRecentContent(Pageable pageable);
    
    ContentDto updateContent(ContentDto contentDto);
    
    void deleteContent(UUID id);
    
    ContentDto toggleContentAvailability(UUID contentId);
    
    Page<EpisodeDto> getEpisodesByContentId(UUID contentId, Pageable pageable);
    
    Page<GenreDto> getGenresByContentId(UUID contentId, Pageable pageable);
    
    void addGenreToContent(UUID contentId, UUID genreId);
    
    void removeGenreFromContent(UUID contentId, UUID genreId);
    
    Page<ContentDto> getPopularContent(Pageable pageable);
    
    Page<ContentDto> getContentRecommendations(UUID userId, Pageable pageable);
}