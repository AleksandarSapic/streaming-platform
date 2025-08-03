package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
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
                .orElseThrow(() -> new RuntimeException("Content type not found"));
        content.setContentType(contentType);
        
        Content savedContent = contentRepository.save(content);
        
        // Add genres if provided
        if (createContentDto.genreIds() != null && !createContentDto.genreIds().isEmpty()) {
            for (UUID genreId : createContentDto.genreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new RuntimeException("Genre not found: " + genreId));
                
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
    public List<ContentDto> getAllContent() {
        List<Content> contents = contentRepository.findAll();
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getAvailableContent() {
        List<Content> contents = contentRepository.findByIsAvailable(true);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByType(String typeName) {
        List<Content> contents = contentRepository.findByContentTypeName(typeName);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByGenre(String genreName) {
        List<Content> contents = contentRepository.findByGenreName(genreName);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> searchContentByTitle(String title) {
        List<Content> contents = contentRepository.findByTitleContainingIgnoreCase(title);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByLanguage(String language) {
        List<Content> contents = contentRepository.findByLanguage(language);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Content> contents = contentRepository.findByReleaseDateBetween(startDate, endDate);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getRecentContent() {
        List<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc();
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    public ContentDto updateContent(ContentDto contentDto) {
        Content existingContent = contentRepository.findById(contentDto.id())
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
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
                    .orElseThrow(() -> new RuntimeException("Content type not found"));
            existingContent.setContentType(contentType);
        }
        
        Content savedContent = contentRepository.save(existingContent);
        return dtoMapper.toContentDto(savedContent);
    }
    
    @Override
    public void deleteContent(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new RuntimeException("Content not found");
        }
        contentRepository.deleteById(id);
    }
    
    @Override
    public ContentDto toggleContentAvailability(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        content.setAvailable(!content.isAvailable());
        Content savedContent = contentRepository.save(content);
        return dtoMapper.toContentDto(savedContent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDto> getEpisodesByContentId(UUID contentId) {
        List<Episode> episodes = episodeRepository.findByContentIdOrderBySeasonNumberAscEpisodeNumberAsc(contentId);
        return dtoMapper.toEpisodeDtoList(episodes);
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
    public void addGenreToContent(UUID contentId, UUID genreId) {
        if (contentGenreRepository.existsByContentIdAndGenreId(contentId, genreId)) {
            throw new RuntimeException("Genre already assigned to content");
        }
        
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        
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
    public List<ContentDto> getPopularContent() {
        // TODO: Implement popularity algorithm based on watchlist counts, ratings, etc.
        // For now, return recent available content
        List<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc();
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentRecommendations(UUID userId) {
        // TODO: Implement recommendation algorithm based on user preferences, watch history, etc.
        // For now, return recent available content
        List<Content> contents = contentRepository.findAvailableContentOrderByReleaseDateDesc();
        return dtoMapper.toContentDtoList(contents);
    }
}