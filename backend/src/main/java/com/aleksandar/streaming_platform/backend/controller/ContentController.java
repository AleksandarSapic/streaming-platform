package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    
    private final ContentService contentService;
    
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @PostMapping
    public ResponseEntity<ContentDto> createContent(@Valid @RequestBody CreateContentDto createContentDto) {
        ContentDto content = contentService.createContent(createContentDto);
        return new ResponseEntity<>(content, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable UUID id) {
        return contentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<ContentDto>> getAllContent() {
        List<ContentDto> content = contentService.getAllContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ContentDto>> getAvailableContent() {
        List<ContentDto> content = contentService.getAvailableContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ContentDto>> searchContent(@RequestParam String title) {
        List<ContentDto> content = contentService.searchContentByTitle(title);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-type")
    public ResponseEntity<List<ContentDto>> getContentByType(@RequestParam String type) {
        List<ContentDto> content = contentService.getContentByType(type);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-genre")
    public ResponseEntity<List<ContentDto>> getContentByGenre(@RequestParam String genre) {
        List<ContentDto> content = contentService.getContentByGenre(genre);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-language")
    public ResponseEntity<List<ContentDto>> getContentByLanguage(@RequestParam String language) {
        List<ContentDto> content = contentService.getContentByLanguage(language);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-date-range")
    public ResponseEntity<List<ContentDto>> getContentByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ContentDto> content = contentService.getContentByDateRange(startDate, endDate);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<ContentDto>> getRecentContent() {
        List<ContentDto> content = contentService.getRecentContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<ContentDto>> getPopularContent() {
        List<ContentDto> content = contentService.getPopularContent();
        return ResponseEntity.ok(content);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContentDto> updateContent(@PathVariable UUID id, @Valid @RequestBody ContentDto contentDto) {
        if (!id.equals(contentDto.id())) {
            return ResponseEntity.badRequest().build();
        }
        ContentDto updatedContent = contentService.updateContent(contentDto);
        return ResponseEntity.ok(updatedContent);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable UUID id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/toggle-availability")
    public ResponseEntity<ContentDto> toggleAvailability(@PathVariable UUID id) {
        ContentDto content = contentService.toggleContentAvailability(id);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}/episodes")
    public ResponseEntity<List<EpisodeDto>> getContentEpisodes(@PathVariable UUID id) {
        List<EpisodeDto> episodes = contentService.getEpisodesByContentId(id);
        return ResponseEntity.ok(episodes);
    }
    
    @GetMapping("/{id}/genres")
    public ResponseEntity<List<GenreDto>> getContentGenres(@PathVariable UUID id) {
        List<GenreDto> genres = contentService.getGenresByContentId(id);
        return ResponseEntity.ok(genres);
    }
    
    @PostMapping("/{id}/genres")
    public ResponseEntity<Void> addGenreToContent(@PathVariable UUID id, @RequestParam UUID genreId) {
        contentService.addGenreToContent(id, genreId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}/genres/{genreId}")
    public ResponseEntity<Void> removeGenreFromContent(@PathVariable UUID id, @PathVariable UUID genreId) {
        contentService.removeGenreFromContent(id, genreId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<List<ContentDto>> getRecommendations(@RequestParam UUID userId) {
        List<ContentDto> recommendations = contentService.getContentRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }
}