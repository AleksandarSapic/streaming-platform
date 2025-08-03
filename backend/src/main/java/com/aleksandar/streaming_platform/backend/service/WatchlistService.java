package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface WatchlistService {
    
    WatchlistDto addToWatchlist(UUID userId, UUID contentId);
    
    void removeFromWatchlist(UUID userId, UUID contentId);
    
    List<WatchlistDto> getWatchlistByUserId(UUID userId);
    
    List<WatchlistDto> getWatchlistByUserIdOrderedByDate(UUID userId);
    
    List<ContentDto> getWatchlistContentByUserId(UUID userId);
    
    List<UserDto> getUsersByContentInWatchlist(UUID contentId);
    
    boolean isContentInUserWatchlist(UUID userId, UUID contentId);
    
    Long getWatchlistCountByUserId(UUID userId);
    
    Long getWatchlistCountByContentId(UUID contentId);
    
    void clearUserWatchlist(UUID userId);
    
    List<ContentDto> getWatchlistContentByUserIdAndGenre(UUID userId, String genreName);
    
    List<ContentDto> getWatchlistContentByUserIdAndType(UUID userId, String typeName);
    
    boolean transferWatchlist(UUID fromUserId, UUID toUserId);
}