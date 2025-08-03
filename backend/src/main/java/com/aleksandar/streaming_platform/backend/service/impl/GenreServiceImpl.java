package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Genre;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.ContentGenre;
import com.aleksandar.streaming_platform.backend.repository.GenreRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentGenreRepository;
import com.aleksandar.streaming_platform.backend.service.GenreService;
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
            throw new RuntimeException("Genre with name already exists: " + genreDto.name());
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
    public List<GenreDto> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return dtoMapper.toGenreDtoList(genres);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenresOrderedByName() {
        List<Genre> genres = genreRepository.findAllOrderByName();
        return dtoMapper.toGenreDtoList(genres);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> searchGenresByName(String name) {
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(name);
        return dtoMapper.toGenreDtoList(genres);
    }
    
    @Override
    public GenreDto updateGenre(GenreDto genreDto) {
        Genre existingGenre = genreRepository.findById(genreDto.id())
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        
        // Check if new name conflicts with existing genre (excluding current genre)
        if (!existingGenre.getName().equals(genreDto.name()) && 
            genreRepository.existsByName(genreDto.name())) {
            throw new RuntimeException("Genre with name already exists: " + genreDto.name());
        }
        
        existingGenre.setName(genreDto.name());
        Genre savedGenre = genreRepository.save(existingGenre);
        return dtoMapper.toGenreDto(savedGenre);
    }
    
    @Override
    public void deleteGenre(UUID id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found");
        }
        
        // Check if any content is assigned this genre
        Long contentCount = getContentCountByGenreId(id);
        if (contentCount > 0) {
            throw new RuntimeException("Cannot delete genre. " + contentCount + " content items are assigned this genre.");
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
    public List<ContentDto> getContentByGenreId(UUID genreId) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByGenreId(genreId);
        List<Content> contents = contentGenres.stream()
                .map(ContentGenre::getContent)
                .collect(Collectors.toList());
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByGenreName(String genreName) {
        List<Content> contents = contentRepository.findByGenreName(genreName);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getContentCountByGenreId(UUID genreId) {
        return contentGenreRepository.countByGenreId(genreId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> getPopularGenres() {
        // TODO: Implement popularity algorithm based on content count, user preferences, etc.
        // For now, return all genres ordered by name
        return getAllGenresOrderedByName();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> getGenresByContentId(UUID contentId) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByContentId(contentId);
        List<Genre> genres = contentGenres.stream()
                .map(ContentGenre::getGenre)
                .collect(Collectors.toList());
        return dtoMapper.toGenreDtoList(genres);
    }
}