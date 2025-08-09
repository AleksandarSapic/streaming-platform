package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WatchlistService {
    
    WatchlistDto addToWatchlist(UUID userId, UUID contentId);
    
    void removeFromWatchlist(UUID userId, UUID contentId);
    
    Page<WatchlistDto> getWatchlistByUserId(UUID userId, Pageable pageable);
    
    Page<WatchlistDto> getWatchlistByUserIdOrderedByDate(UUID userId, Pageable pageable);
    
    Page<ContentDto> getWatchlistContentByUserId(UUID userId, Pageable pageable);
    
    Page<UserDto> getUsersByContentInWatchlist(UUID contentId, Pageable pageable);
    
    boolean isContentInUserWatchlist(UUID userId, UUID contentId);
    
    Long getWatchlistCountByUserId(UUID userId);
    
    Long getWatchlistCountByContentId(UUID contentId);
    
    void clearUserWatchlist(UUID userId);
    
    Page<ContentDto> getWatchlistContentByUserIdAndGenre(UUID userId, String genreName, Pageable pageable);
    
    Page<ContentDto> getWatchlistContentByUserIdAndType(UUID userId, String typeName, Pageable pageable);
    
    boolean transferWatchlist(UUID fromUserId, UUID toUserId);
}