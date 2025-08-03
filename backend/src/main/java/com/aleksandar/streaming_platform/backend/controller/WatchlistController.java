package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.WatchlistDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.UserDto;
import com.aleksandar.streaming_platform.backend.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {
    
    private final WatchlistService watchlistService;
    
    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }
    
    @PostMapping
    public ResponseEntity<WatchlistDto> addToWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        WatchlistDto watchlist = watchlistService.addToWatchlist(userId, contentId);
        return new ResponseEntity<>(watchlist, HttpStatus.CREATED);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> removeFromWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        watchlistService.removeFromWatchlist(userId, contentId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WatchlistDto>> getUserWatchlist(@PathVariable UUID userId) {
        List<WatchlistDto> watchlist = watchlistService.getWatchlistByUserIdOrderedByDate(userId);
        return ResponseEntity.ok(watchlist);
    }
    
    @GetMapping("/user/{userId}/content")
    public ResponseEntity<List<ContentDto>> getUserWatchlistContent(@PathVariable UUID userId) {
        List<ContentDto> content = watchlistService.getWatchlistContentByUserId(userId);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/content/{contentId}/users")
    public ResponseEntity<List<UserDto>> getUsersByContentInWatchlist(@PathVariable UUID contentId) {
        List<UserDto> users = watchlistService.getUsersByContentInWatchlist(contentId);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Boolean> isContentInWatchlist(@RequestParam UUID userId, @RequestParam UUID contentId) {
        boolean isInWatchlist = watchlistService.isContentInUserWatchlist(userId, contentId);
        return ResponseEntity.ok(isInWatchlist);
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserWatchlistCount(@PathVariable UUID userId) {
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
        watchlistService.clearUserWatchlist(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}/content/by-genre")
    public ResponseEntity<List<ContentDto>> getWatchlistByGenre(@PathVariable UUID userId, @RequestParam String genre) {
        List<ContentDto> content = watchlistService.getWatchlistContentByUserIdAndGenre(userId, genre);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/user/{userId}/content/by-type")
    public ResponseEntity<List<ContentDto>> getWatchlistByType(@PathVariable UUID userId, @RequestParam String type) {
        List<ContentDto> content = watchlistService.getWatchlistContentByUserIdAndType(userId, type);
        return ResponseEntity.ok(content);
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<Boolean> transferWatchlist(@RequestParam UUID fromUserId, @RequestParam UUID toUserId) {
        boolean success = watchlistService.transferWatchlist(fromUserId, toUserId);
        return ResponseEntity.ok(success);
    }
}