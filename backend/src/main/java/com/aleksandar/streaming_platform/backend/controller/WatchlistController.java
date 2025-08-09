package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.security.AuthorizationService;
import com.aleksandar.streaming_platform.backend.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {
    
    private final WatchlistService watchlistService;
    private final AuthorizationService authorizationService;
    
    public WatchlistController(WatchlistService watchlistService, AuthorizationService authorizationService) {
        this.watchlistService = watchlistService;
        this.authorizationService = authorizationService;
    }
    
    @PostMapping
    public ResponseEntity<WatchlistDto> addToWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(userId);
        WatchlistDto watchlist = watchlistService.addToWatchlist(userId, contentId);
        return new ResponseEntity<>(watchlist, HttpStatus.CREATED);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> removeFromWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(userId);
        watchlistService.removeFromWatchlist(userId, contentId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<WatchlistDto>> getUserWatchlist(@PathVariable UUID userId, Pageable pageable) {
        authorizationService.validateWatchlistAccess(userId);
        Page<WatchlistDto> watchlist = watchlistService.getWatchlistByUserIdOrderedByDate(userId, pageable);
        return ResponseEntity.ok(watchlist);
    }
    
    @GetMapping("/user/{userId}/content")
    public ResponseEntity<Page<ContentDto>> getUserWatchlistContent(@PathVariable UUID userId, Pageable pageable) {
        authorizationService.validateWatchlistAccess(userId);
        Page<ContentDto> content = watchlistService.getWatchlistContentByUserId(userId, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/content/{contentId}/users")
    public ResponseEntity<Page<UserDto>> getUsersByContentInWatchlist(@PathVariable UUID contentId, Pageable pageable) {
        authorizationService.validateAdminAccess();
        Page<UserDto> users = watchlistService.getUsersByContentInWatchlist(contentId, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> isContentInWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        authorizationService.validateWatchlistAccess(userId);
        boolean isInWatchlist = watchlistService.isContentInUserWatchlist(userId, contentId);
        return ResponseEntity.ok(isInWatchlist);
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserWatchlistCount(@PathVariable UUID userId) {
        authorizationService.validateWatchlistAccess(userId);
        Long count = watchlistService.getWatchlistCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/content/{contentId}/count")
    public ResponseEntity<Long> getContentWatchlistCount(@PathVariable UUID contentId) {
        Long count = watchlistService.getWatchlistCountByContentId(contentId);
        return ResponseEntity.ok(count);
    }
    
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<Void> clearUserWatchlist(@PathVariable UUID userId) {
        authorizationService.validateWatchlistAccess(userId);
        watchlistService.clearUserWatchlist(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}/content/by-genre")
    public ResponseEntity<Page<ContentDto>> getWatchlistByGenre(@PathVariable UUID userId, @RequestParam String genre, Pageable pageable) {
        authorizationService.validateWatchlistAccess(userId);
        Page<ContentDto> content = watchlistService.getWatchlistContentByUserIdAndGenre(userId, genre, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/user/{userId}/content/by-type")
    public ResponseEntity<Page<ContentDto>> getWatchlistByType(@PathVariable UUID userId, @RequestParam String type, Pageable pageable) {
        authorizationService.validateWatchlistAccess(userId);
        Page<ContentDto> content = watchlistService.getWatchlistContentByUserIdAndType(userId, type, pageable);
        return ResponseEntity.ok(content);
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<Boolean> transferWatchlist(@RequestParam UUID fromUserId, @RequestParam UUID toUserId) {
        authorizationService.validateWatchlistAccess(fromUserId);
        authorizationService.validateWatchlistAccess(toUserId);
        boolean success = watchlistService.transferWatchlist(fromUserId, toUserId);
        return ResponseEntity.ok(success);
    }
}