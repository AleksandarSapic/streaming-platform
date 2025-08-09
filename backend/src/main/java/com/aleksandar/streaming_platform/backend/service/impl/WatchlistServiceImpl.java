package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.Watchlist;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import com.aleksandar.streaming_platform.backend.repository.WatchlistRepository;
import com.aleksandar.streaming_platform.backend.service.WatchlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));
        
        if (watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new BusinessLogicException("Content already in watchlist");
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
            throw new BusinessLogicException("Content not in watchlist");
        }
        watchlistRepository.deleteByUserIdAndContentId(userId, contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<WatchlistDto> getWatchlistByUserId(UUID userId, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserId(userId, pageable);
        return watchlists.map(dtoMapper::toWatchlistDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<WatchlistDto> getWatchlistByUserIdOrderedByDate(UUID userId, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId, pageable);
        return watchlists.map(dtoMapper::toWatchlistDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getWatchlistContentByUserId(UUID userId, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserIdOrderByAddedAtDesc(userId, pageable);
        return watchlists.map(watchlist -> dtoMapper.toContentDto(watchlist.getContent()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByContentInWatchlist(UUID contentId, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByContentId(contentId, pageable);
        return watchlists.map(watchlist -> dtoMapper.toUserDto(watchlist.getUser()));
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
        List<Watchlist> watchlists = watchlistRepository.findByUserIdList(userId);
        watchlistRepository.deleteAll(watchlists);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getWatchlistContentByUserIdAndGenre(UUID userId, String genreName, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserIdAndContentGenreName(userId, genreName, pageable);
        return watchlists.map(watchlist -> dtoMapper.toContentDto(watchlist.getContent()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getWatchlistContentByUserIdAndType(UUID userId, String typeName, Pageable pageable) {
        Page<Watchlist> watchlists = watchlistRepository.findByUserIdAndContentTypeName(userId, typeName, pageable);
        return watchlists.map(watchlist -> dtoMapper.toContentDto(watchlist.getContent()));
    }
    
    @Override
    public boolean transferWatchlist(UUID fromUserId, UUID toUserId) {
        try {
            // Validate both users exist
            User fromUser = userRepository.findById(fromUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", fromUserId));
            User toUser = userRepository.findById(toUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", toUserId));
            
            List<Watchlist> fromWatchlists = watchlistRepository.findByUserIdList(fromUserId);
            
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