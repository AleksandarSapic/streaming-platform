package com.aleksandar.streaming_platform.backend.mapper;

import com.aleksandar.streaming_platform.backend.dto.*;
import com.aleksandar.streaming_platform.backend.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
    
    // User mappings
    public UserDto toUserDto(User user) {
        if (user == null) return null;
        
        return new UserDto(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getCountry(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            toUserRoleDto(user.getUserRole())
        );
    }
    
    public User toUserEntity(CreateUserDto createUserDto) {
        if (createUserDto == null) return null;
        
        User user = new User();
        user.setFullName(createUserDto.fullName());
        user.setEmail(createUserDto.email());
        user.setHashedPassword(createUserDto.password()); // Should be hashed in service
        user.setCountry(createUserDto.country());
        return user;
    }
    
    public User toUserEntity(UserDto userDto) {
        if (userDto == null) return null;
        
        User user = new User();
        user.setId(userDto.id());
        user.setFullName(userDto.fullName());
        user.setEmail(userDto.email());
        user.setCountry(userDto.country());
        user.setCreatedAt(userDto.createdAt());
        user.setUpdatedAt(userDto.updatedAt());
        return user;
    }
    
    // UserRole mappings
    public UserRoleDto toUserRoleDto(UserRole userRole) {
        if (userRole == null) return null;
        
        return new UserRoleDto(
            userRole.getId(),
            userRole.getName()
        );
    }
    
    public UserRole toUserRoleEntity(UserRoleDto userRoleDto) {
        if (userRoleDto == null) return null;
        
        UserRole userRole = new UserRole();
        userRole.setId(userRoleDto.id());
        userRole.setName(userRoleDto.name());
        return userRole;
    }
    
    
    // Content mappings
    public ContentDto toContentDto(Content content) {
        if (content == null) return null;
        
        Integer episodeCount = null;
        if (content.getEpisodes() != null) {
            episodeCount = content.getEpisodes().size();
        }
        
        List<GenreDto> genres = null;
        if (content.getContentGenres() != null) {
            genres = content.getContentGenres().stream()
                .map(cg -> toGenreDto(cg.getGenre()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        return new ContentDto(
            content.getId(),
            content.getTitle(),
            content.getDescription(),
            content.getReleaseDate(),
            content.getDuration(),
            content.getLanguage(),
            content.getThumbnailUrl(),
            content.getVideoUrl(),
            content.isAvailable(),
            content.getCreatedAt(),
            content.getUpdatedAt(),
            toContentTypeDto(content.getContentType()),
            genres,
            episodeCount
        );
    }
    
    public Content toContentEntity(CreateContentDto createContentDto) {
        if (createContentDto == null) return null;
        
        Content content = new Content();
        content.setTitle(createContentDto.title());
        content.setDescription(createContentDto.description());
        content.setReleaseDate(createContentDto.releaseDate());
        content.setDuration(createContentDto.duration());
        content.setLanguage(createContentDto.language());
        content.setThumbnailUrl(createContentDto.thumbnailUrl());
        content.setVideoUrl(createContentDto.videoUrl());
        content.setAvailable(createContentDto.isAvailable());
        return content;
    }
    
    public Content toContentEntity(ContentDto contentDto) {
        if (contentDto == null) return null;
        
        Content content = new Content();
        content.setId(contentDto.id());
        content.setTitle(contentDto.title());
        content.setDescription(contentDto.description());
        content.setReleaseDate(contentDto.releaseDate());
        content.setDuration(contentDto.duration());
        content.setLanguage(contentDto.language());
        content.setThumbnailUrl(contentDto.thumbnailUrl());
        content.setVideoUrl(contentDto.videoUrl());
        content.setAvailable(contentDto.isAvailable());
        content.setCreatedAt(contentDto.createdAt());
        content.setUpdatedAt(contentDto.updatedAt());
        return content;
    }
    
    // ContentType mappings
    public ContentTypeDto toContentTypeDto(ContentType contentType) {
        if (contentType == null) return null;
        
        return new ContentTypeDto(
            contentType.getId(),
            contentType.getName()
        );
    }
    
    public ContentType toContentTypeEntity(ContentTypeDto contentTypeDto) {
        if (contentTypeDto == null) return null;
        
        ContentType contentType = new ContentType();
        contentType.setId(contentTypeDto.id());
        contentType.setName(contentTypeDto.name());
        return contentType;
    }
    
    // Genre mappings
    public GenreDto toGenreDto(Genre genre) {
        if (genre == null) return null;
        
        return new GenreDto(
            genre.getId(),
            genre.getName()
        );
    }
    
    public Genre toGenreEntity(GenreDto genreDto) {
        if (genreDto == null) return null;
        
        Genre genre = new Genre();
        genre.setId(genreDto.id());
        genre.setName(genreDto.name());
        return genre;
    }
    
    // Episode mappings
    public EpisodeDto toEpisodeDto(Episode episode) {
        if (episode == null) return null;
        
        UUID contentId = null;
        String contentTitle = null;
        if (episode.getContent() != null) {
            contentId = episode.getContent().getId();
            contentTitle = episode.getContent().getTitle();
        }
        
        return new EpisodeDto(
            episode.getId(),
            episode.getSeasonNumber(),
            episode.getEpisodeNumber(),
            episode.getTitle(),
            episode.getDescription(),
            episode.getDuration(),
            episode.getReleaseDate(),
            episode.getThumbnailUrl(),
            episode.getVideoUrl(),
            episode.getCreatedAt(),
            episode.getUpdatedAt(),
            contentId,
            contentTitle
        );
    }
    
    public Episode toEpisodeEntity(CreateEpisodeDto createEpisodeDto) {
        if (createEpisodeDto == null) return null;
        
        Episode episode = new Episode();
        episode.setSeasonNumber(createEpisodeDto.seasonNumber());
        episode.setEpisodeNumber(createEpisodeDto.episodeNumber());
        episode.setTitle(createEpisodeDto.title());
        episode.setDescription(createEpisodeDto.description());
        episode.setDuration(createEpisodeDto.duration());
        episode.setReleaseDate(createEpisodeDto.releaseDate());
        episode.setThumbnailUrl(createEpisodeDto.thumbnailUrl());
        episode.setVideoUrl(createEpisodeDto.videoUrl());
        return episode;
    }
    
    public Episode toEpisodeEntity(EpisodeDto episodeDto) {
        if (episodeDto == null) return null;
        
        Episode episode = new Episode();
        episode.setId(episodeDto.id());
        episode.setSeasonNumber(episodeDto.seasonNumber());
        episode.setEpisodeNumber(episodeDto.episodeNumber());
        episode.setTitle(episodeDto.title());
        episode.setDescription(episodeDto.description());
        episode.setDuration(episodeDto.duration());
        episode.setReleaseDate(episodeDto.releaseDate());
        episode.setThumbnailUrl(episodeDto.thumbnailUrl());
        episode.setVideoUrl(episodeDto.videoUrl());
        episode.setCreatedAt(episodeDto.createdAt());
        episode.setUpdatedAt(episodeDto.updatedAt());
        return episode;
    }
    
    // Watchlist mappings
    public WatchlistDto toWatchlistDto(Watchlist watchlist) {
        if (watchlist == null) return null;
        
        return new WatchlistDto(
            watchlist.getUserId(),
            watchlist.getContentId(),
            watchlist.getAddedAt(),
            toUserDto(watchlist.getUser()),
            toContentDto(watchlist.getContent())
        );
    }
    
    // List mappings
    public List<UserDto> toUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }
    
    public List<ContentDto> toContentDtoList(List<Content> contents) {
        return contents.stream().map(this::toContentDto).collect(Collectors.toList());
    }
    
    public List<EpisodeDto> toEpisodeDtoList(List<Episode> episodes) {
        return episodes.stream().map(this::toEpisodeDto).collect(Collectors.toList());
    }
    
    public List<GenreDto> toGenreDtoList(List<Genre> genres) {
        return genres.stream().map(this::toGenreDto).collect(Collectors.toList());
    }
    
    public List<ContentTypeDto> toContentTypeDtoList(List<ContentType> contentTypes) {
        return contentTypes.stream().map(this::toContentTypeDto).collect(Collectors.toList());
    }
    
    public List<UserRoleDto> toUserRoleDtoList(List<UserRole> userRoles) {
        return userRoles.stream().map(this::toUserRoleDto).collect(Collectors.toList());
    }
    
    public List<WatchlistDto> toWatchlistDtoList(List<Watchlist> watchlists) {
        return watchlists.stream().map(this::toWatchlistDto).collect(Collectors.toList());
    }
}