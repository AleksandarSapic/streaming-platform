package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.ContentType;
import com.aleksandar.streaming_platform.backend.model.Episode;
import com.aleksandar.streaming_platform.backend.model.Genre;
import com.aleksandar.streaming_platform.backend.model.ContentGenre;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentTypeRepository;
import com.aleksandar.streaming_platform.backend.repository.EpisodeRepository;
import com.aleksandar.streaming_platform.backend.repository.GenreRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentGenreRepository;
import com.aleksandar.streaming_platform.backend.service.ContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {
    
    private final ContentRepository contentRepository;
    private final ContentTypeRepository contentTypeRepository;
    private final EpisodeRepository episodeRepository;
    private final GenreRepository genreRepository;
    private final ContentGenreRepository contentGenreRepository;
    private final DtoMapper dtoMapper;
    
    public ContentServiceImpl(ContentRepository contentRepository,
                             ContentTypeRepository contentTypeRepository,
                             EpisodeRepository episodeRepository,
                             GenreRepository genreRepository,
                             ContentGenreRepository contentGenreRepository,
                             DtoMapper dtoMapper) {
        this.contentRepository = contentRepository;
        this.contentTypeRepository = contentTypeRepository;
        this.episodeRepository = episodeRepository;
        this.genreRepository = genreRepository;
        this.contentGenreRepository = contentGenreRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public ContentDto createContent(CreateContentDto createContentDto) {
        Content content = dtoMapper.toContentEntity(createContentDto);
        
        // Set content type
        ContentType contentType = contentTypeRepository.findById(createContentDto.contentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("ContentType", "id", createContentDto.contentTypeId()));
        content.setContentType(contentType);
        
        Content savedContent = contentRepository.save(content);
        
        // Add genres if provided
        if (createContentDto.genreIds() != null && !createContentDto.genreIds().isEmpty()) {
            for (UUID genreId : createContentDto.genreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", genreId));
                
                ContentGenre contentGenre = new ContentGenre();
                contentGenre.setContentId(savedContent.getId());
                contentGenre.setGenreId(genreId);
                contentGenre.setContent(savedContent);
                contentGenre.setGenre(genre);
                contentGenreRepository.save(contentGenre);
            }
        }
        
        return dtoMapper.toContentDto(savedContent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContentDto> getContentById(UUID id) {
        return contentRepository.findById(id)
                .map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getAllContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findAll(pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getAvailableContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findByIsAvailable(true, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByType(String typeName, Pageable pageable) {
        Page<Content> contents = contentRepository.findByContentTypeName(typeName, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByGenre(String genreName, Pageable pageable) {
        Page<Content> contents = contentRepository.findByGenreName(genreName, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> searchContentByTitle(String title, Pageable pageable) {
        Page<Content> contents = contentRepository.findByTitleContainingIgnoreCase(title, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByLanguage(String language, Pageable pageable) {
        Page<Content> contents = contentRepository.findByLanguage(language, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Content> contents = contentRepository.findByReleaseDateBetween(startDate, endDate, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getRecentContent(Pageable pageable) {
        Page<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc(pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    public ContentDto updateContent(ContentDto contentDto) {
        Content existingContent = contentRepository.findById(contentDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentDto.id()));
        
        existingContent.setTitle(contentDto.title());
        existingContent.setDescription(contentDto.description());
        existingContent.setReleaseDate(contentDto.releaseDate());
        existingContent.setDuration(contentDto.duration());
        existingContent.setLanguage(contentDto.language());
        existingContent.setThumbnailUrl(contentDto.thumbnailUrl());
        existingContent.setVideoUrl(contentDto.videoUrl());
        existingContent.setAvailable(contentDto.isAvailable());
        
        if (contentDto.contentType() != null) {
            ContentType contentType = contentTypeRepository.findById(contentDto.contentType().id())
                    .orElseThrow(() -> new ResourceNotFoundException("ContentType", "id", contentDto.contentType().id()));
            existingContent.setContentType(contentType);
        }
        
        Content savedContent = contentRepository.save(existingContent);
        return dtoMapper.toContentDto(savedContent);
    }
    
    @Override
    public void deleteContent(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Content", "id", id);
        }
        contentRepository.deleteById(id);
    }
    
    @Override
    public ContentDto toggleContentAvailability(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        content.setAvailable(!content.isAvailable());
        Content savedContent = contentRepository.save(content);
        return dtoMapper.toContentDto(savedContent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<EpisodeDto> getEpisodesByContentId(UUID contentId, Pageable pageable) {
        Page<Episode> episodes = episodeRepository.findByContentIdOrderBySeasonNumberAscEpisodeNumberAsc(contentId, pageable);
        return episodes.map(dtoMapper::toEpisodeDto);
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
    
    @Override
    public void addGenreToContent(UUID contentId, UUID genreId) {
        if (contentGenreRepository.existsByContentIdAndGenreId(contentId, genreId)) {
            throw new BusinessLogicException("Genre already assigned to content");
        }
        
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", genreId));
        
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
            throw new BusinessLogicException("Genre not assigned to content");
        }
        contentGenreRepository.deleteByContentIdAndGenreId(contentId, genreId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getPopularContent(Pageable pageable) {
        // TODO: Implement popularity algorithm based on watchlist counts, ratings, etc.
        // For now, return recent available content
        Page<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc(pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentRecommendations(UUID userId, Pageable pageable) {
        // TODO: Implement recommendation algorithm based on user preferences, watch history, etc.
        // For now, return recent available content
        Page<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc(pageable);
        return contents.map(dtoMapper::toContentDto);
    }
}