package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Watchlist;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.repository.WatchlistRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.service.WatchlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {
    
    private final WatchlistRepository watchlistRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;
    
    public WatchlistServiceImpl(WatchlistRepository watchlistRepository,
                               ContentRepository contentRepository,
                               UserRepository userRepository,
                               DtoMapper dtoMapper) {
        this.watchlistRepository = watchlistRepository;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public WatchlistDto addToWatchlist(UUID userId, UUID contentId) {
        // Validate user and content exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        if (watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new RuntimeException("Content already in watchlist");
        }
        
        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setContentId(contentId);
        watchlist.setUser(user);
        watchlist.setContent(content);
        
        Watchlist savedWatchlist = watchlistRepository.save(watchlist);
        return dtoMapper.toWatchlistDto(savedWatchlist);
    }
    
    @Override
    public void removeFromWatchlist(UUID userId, UUID contentId) {
        if (!watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new RuntimeException("Content not in watchlist");
        }
        watchlistRepository.deleteByUserIdAndContentId(userId, contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDto> getWatchlistByUserId(UUID userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserId(userId);
        return dtoMapper.toWatchlistDtoList(watchlists);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WatchlistDto> getWatchlistByUserIdOrderedByDate(UUID userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        return dtoMapper.toWatchlistDtoList(watchlists);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getWatchlistContentByUserId(UUID userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        List<Content> contents = watchlists.stream()
                .map(Watchlist::getContent)
                .collect(Collectors.toList());
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByContentInWatchlist(UUID contentId) {
        List<Watchlist> watchlists = watchlistRepository.findByContentId(contentId);
        List<User> users = watchlists.stream()
                .map(Watchlist::getUser)
                .collect(Collectors.toList());
        return dtoMapper.toUserDtoList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContentInUserWatchlist(UUID userId, UUID contentId) {
        return watchlistRepository.existsByUserIdAndContentId(userId, contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getWatchlistCountByUserId(UUID userId) {
        return watchlistRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getWatchlistCountByContentId(UUID contentId) {
        return watchlistRepository.countByContentId(contentId);
    }
    
    @Override
    public void clearUserWatchlist(UUID userId) {
        List<Watchlist> watchlists = watchlistRepository.findByUserId(userId);
        watchlistRepository.deleteAll(watchlists);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getWatchlistContentByUserIdAndGenre(UUID userId, String genreName) {
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        List<Content> contents = watchlists.stream()
                .map(Watchlist::getContent)
                .filter(content -> content.getContentGenres().stream()
                        .anyMatch(cg -> cg.getGenre().getName().equalsIgnoreCase(genreName)))
                .collect(Collectors.toList());
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getWatchlistContentByUserIdAndType(UUID userId, String typeName) {
        List<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId);
        List<Content> contents = watchlists.stream()
                .map(Watchlist::getContent)
                .filter(content -> content.getContentType().getName().equalsIgnoreCase(typeName))
                .collect(Collectors.toList());
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    public boolean transferWatchlist(UUID fromUserId, UUID toUserId) {
        try {
            // Validate both users exist
            User fromUser = userRepository.findById(fromUserId)
                    .orElseThrow(() -> new RuntimeException("Source user not found"));
            User toUser = userRepository.findById(toUserId)
                    .orElseThrow(() -> new RuntimeException("Target user not found"));
            
            List<Watchlist> fromWatchlists = watchlistRepository.findByUserId(fromUserId);
            
            for (Watchlist watchlist : fromWatchlists) {
                // Check if target user already has this content in watchlist
                if (!watchlistRepository.existsByUserIdAndContentId(toUserId, watchlist.getContentId())) {
                    Watchlist newWatchlist = new Watchlist();
                    newWatchlist.setUserId(toUserId);
                    newWatchlist.setContentId(watchlist.getContentId());
                    newWatchlist.setUser(toUser);
                    newWatchlist.setContent(watchlist.getContent());
                    watchlistRepository.save(newWatchlist);
                }
            }
            
            // Optionally clear the source user's watchlist
            // clearUserWatchlist(fromUserId);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}