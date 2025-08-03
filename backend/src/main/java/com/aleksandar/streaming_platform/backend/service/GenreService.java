package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenreService {
    
    GenreDto createGenre(GenreDto genreDto);
    
    Optional<GenreDto> getGenreById(UUID id);
    
    Optional<GenreDto> getGenreByName(String name);
    
    List<GenreDto> getAllGenres();
    
    List<GenreDto> getAllGenresOrderedByName();
    
    List<GenreDto> searchGenresByName(String name);
    
    GenreDto updateGenre(GenreDto genreDto);
    
    void deleteGenre(UUID id);
    
    boolean existsByName(String name);
    
    List<ContentDto> getContentByGenreId(UUID genreId);
    
    List<ContentDto> getContentByGenreName(String genreName);
    
    Long getContentCountByGenreId(UUID genreId);
    
    List<GenreDto> getPopularGenres();
    
    List<GenreDto> getGenresByContentId(UUID contentId);
}