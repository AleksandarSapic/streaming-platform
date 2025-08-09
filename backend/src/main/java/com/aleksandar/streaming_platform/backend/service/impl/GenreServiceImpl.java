package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.DuplicateResourceException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.ContentGenre;
import com.aleksandar.streaming_platform.backend.model.Genre;
import com.aleksandar.streaming_platform.backend.repository.ContentGenreRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.GenreRepository;
import com.aleksandar.streaming_platform.backend.service.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GenreServiceImpl implements GenreService {
    
    private final GenreRepository genreRepository;
    private final ContentRepository contentRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final DtoMapper dtoMapper;
    
    public GenreServiceImpl(GenreRepository genreRepository,
                           ContentRepository contentRepository,
                           ContentGenreRepository contentGenreRepository,
                           DtoMapper dtoMapper) {
        this.genreRepository = genreRepository;
        this.contentRepository = contentRepository;
        this.contentGenreRepository = contentGenreRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public GenreDto createGenre(GenreDto genreDto) {
        if (genreRepository.existsByName(genreDto.name())) {
            throw new DuplicateResourceException("Genre", "name", genreDto.name());
        }
        
        Genre genre = new Genre();
        genre.setName(genreDto.name());
        Genre savedGenre = genreRepository.save(genre);
        return dtoMapper.toGenreDto(savedGenre);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<GenreDto> getGenreById(UUID id) {
        return genreRepository.findById(id)
                .map(dtoMapper::toGenreDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<GenreDto> getGenreByName(String name) {
        return genreRepository.findByName(name)
                .map(dtoMapper::toGenreDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> getAllGenres(Pageable pageable) {
        Page<Genre> genres = genreRepository.findAll(pageable);
        return genres.map(dtoMapper::toGenreDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> getAllGenresOrderedByName(Pageable pageable) {
        Page<Genre> genres = genreRepository.findAllOrderByName(pageable);
        return genres.map(dtoMapper::toGenreDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> searchGenresByName(String name, Pageable pageable) {
        Page<Genre> genres = genreRepository.findByNameContainingIgnoreCase(name, pageable);
        return genres.map(dtoMapper::toGenreDto);
    }
    
    @Override
    public GenreDto updateGenre(GenreDto genreDto) {
        Genre existingGenre = genreRepository.findById(genreDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", genreDto.id()));
        
        // Check if new name conflicts with existing genre (excluding current genre)
        if (!existingGenre.getName().equals(genreDto.name()) && 
            genreRepository.existsByName(genreDto.name())) {
            throw new DuplicateResourceException("Genre", "name", genreDto.name());
        }
        
        existingGenre.setName(genreDto.name());
        Genre savedGenre = genreRepository.save(existingGenre);
        return dtoMapper.toGenreDto(savedGenre);
    }
    
    @Override
    public void deleteGenre(UUID id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre", "id", id);
        }
        
        // Check if any content is assigned this genre
        Long contentCount = getContentCountByGenreId(id);
        if (contentCount > 0) {
            throw new BusinessLogicException("Cannot delete genre. " + contentCount + " content items are assigned this genre.");
        }
        
        genreRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return genreRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByGenreId(UUID genreId, Pageable pageable) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByGenreId(genreId);
        // Convert to page manually since ContentGenreRepository doesn't support pagination
        List<ContentDto> contentDtos = contentGenres.stream()
                .map(contentGenre -> dtoMapper.toContentDto(contentGenre.getContent()))
                .collect(Collectors.toList());
        
        // Create a page from the list (simple implementation for pagination)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), contentDtos.size());
        List<ContentDto> pageContent = start >= contentDtos.size() ? List.of() : contentDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, contentDtos.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByGenreName(String genreName, Pageable pageable) {
        Page<Content> contents = contentRepository.findByGenreName(genreName, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getContentCountByGenreId(UUID genreId) {
        return contentGenreRepository.countByGenreId(genreId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> getPopularGenres(Pageable pageable) {
        // TODO: Implement popularity algorithm based on content count, user preferences, etc.
        // For now, return all genres ordered by name
        return getAllGenresOrderedByName(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> getGenresByContentId(UUID contentId, Pageable pageable) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByContentId(contentId);
        // Convert to page manually since ContentGenreRepository doesn't support pagination
        List<GenreDto> genreDtos = contentGenres.stream()
                .map(contentGenre -> dtoMapper.toGenreDto(contentGenre.getGenre()))
                .collect(Collectors.toList());
        
        // Create a page from the list (simple implementation for pagination)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), genreDtos.size());
        List<GenreDto> pageContent = start >= genreDtos.size() ? List.of() : genreDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, genreDtos.size());
    }
}