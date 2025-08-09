package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GenreService {
    
    GenreDto createGenre(GenreDto genreDto);
    
    Optional<GenreDto> getGenreById(UUID id);
    
    Optional<GenreDto> getGenreByName(String name);
    
    Page<GenreDto> getAllGenres(Pageable pageable);
    
    Page<GenreDto> getAllGenresOrderedByName(Pageable pageable);
    
    Page<GenreDto> searchGenresByName(String name, Pageable pageable);
    
    GenreDto updateGenre(GenreDto genreDto);
    
    void deleteGenre(UUID id);
    
    boolean existsByName(String name);
    
    Page<ContentDto> getContentByGenreId(UUID genreId, Pageable pageable);
    
    Page<ContentDto> getContentByGenreName(String genreName, Pageable pageable);
    
    Long getContentCountByGenreId(UUID genreId);
    
    Page<GenreDto> getPopularGenres(Pageable pageable);
    
    Page<GenreDto> getGenresByContentId(UUID contentId, Pageable pageable);
}