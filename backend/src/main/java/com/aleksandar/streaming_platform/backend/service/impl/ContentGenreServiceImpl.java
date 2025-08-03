package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.Genre;
import com.aleksandar.streaming_platform.backend.model.ContentGenre;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.GenreRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentGenreRepository;
import com.aleksandar.streaming_platform.backend.service.ContentGenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentGenreServiceImpl implements ContentGenreService {
    
    private final ContentGenreRepository contentGenreRepository;
    private final ContentRepository contentRepository;
    private final GenreRepository genreRepository;
    private final DtoMapper dtoMapper;
    
    public ContentGenreServiceImpl(ContentGenreRepository contentGenreRepository,
                                  ContentRepository contentRepository,
                                  GenreRepository genreRepository,
                                  DtoMapper dtoMapper) {
        this.contentGenreRepository = contentGenreRepository;
        this.contentRepository = contentRepository;
        this.genreRepository = genreRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public void addGenreToContent(UUID contentId, UUID genreId) {
        // Validate content and genre exist
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        
        if (contentGenreRepository.existsByContentIdAndGenreId(contentId, genreId)) {
            throw new RuntimeException("Genre already assigned to content");
        }
        
        ContentGenre contentGenre = new ContentGenre();
        contentGenre.setContentId(contentId);
        contentGenre.setGenreId(genreId);
        contentGenre.setContent(content);
        contentGenre.setGenre(genre);
        
        contentGenreRepository.save(contentGenre);
    }
    
    @Override
    public void removeGenreFromContent(UUID contentId, UUID genreId) {
        if (!contentGenreRepository.existsByContentIdAndGenreId(contentId, genreId)) {
            throw new RuntimeException("Genre not assigned to content");
        }
        contentGenreRepository.deleteByContentIdAndGenreId(contentId, genreId);
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
    public boolean isGenreAssignedToContent(UUID contentId, UUID genreId) {
        return contentGenreRepository.existsByContentIdAndGenreId(contentId, genreId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getGenreCountByContentId(UUID contentId) {
        return contentGenreRepository.countByContentId(contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getContentCountByGenreId(UUID genreId) {
        return contentGenreRepository.countByGenreId(genreId);
    }
    
    @Override
    public void removeAllGenresFromContent(UUID contentId) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByContentId(contentId);
        contentGenreRepository.deleteAll(contentGenres);
    }
    
    @Override
    public void removeContentFromAllGenres(UUID genreId) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByGenreId(genreId);
        contentGenreRepository.deleteAll(contentGenres);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> searchContentByGenreName(String genreName) {
        List<ContentGenre> contentGenres = contentGenreRepository.findByGenreName(genreName);
        List<Content> contents = contentGenres.stream()
                .map(ContentGenre::getContent)
                .collect(Collectors.toList());
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    public void bulkAssignGenresToContent(UUID contentId, List<UUID> genreIds) {
        // Validate content exists
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        // Remove existing genre assignments
        removeAllGenresFromContent(contentId);
        
        // Add new genre assignments
        for (UUID genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("Genre not found: " + genreId));
            
            ContentGenre contentGenre = new ContentGenre();
            contentGenre.setContentId(contentId);
            contentGenre.setGenreId(genreId);
            contentGenre.setContent(content);
            contentGenre.setGenre(genre);
            
            contentGenreRepository.save(contentGenre);
        }
    }
}