package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateContentDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<Page<ContentDto>> getAllContent(Pageable pageable) {
        Page<ContentDto> content = contentService.getAllContent(pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/available")
    public ResponseEntity<Page<ContentDto>> getAvailableContent(Pageable pageable) {
        Page<ContentDto> content = contentService.getAvailableContent(pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ContentDto>> searchContent(@RequestParam String title, Pageable pageable) {
        Page<ContentDto> content = contentService.searchContentByTitle(title, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-type")
    public ResponseEntity<Page<ContentDto>> getContentByType(@RequestParam String type, Pageable pageable) {
        Page<ContentDto> content = contentService.getContentByType(type, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-genre")
    public ResponseEntity<Page<ContentDto>> getContentByGenre(@RequestParam String genre, Pageable pageable) {
        Page<ContentDto> content = contentService.getContentByGenre(genre, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-language")
    public ResponseEntity<Page<ContentDto>> getContentByLanguage(@RequestParam String language, Pageable pageable) {
        Page<ContentDto> content = contentService.getContentByLanguage(language, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-date-range")
    public ResponseEntity<Page<ContentDto>> getContentByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<ContentDto> content = contentService.getContentByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<Page<ContentDto>> getRecentContent(Pageable pageable) {
        Page<ContentDto> content = contentService.getRecentContent(pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<Page<ContentDto>> getPopularContent(Pageable pageable) {
        Page<ContentDto> content = contentService.getPopularContent(pageable);
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
    public ResponseEntity<Page<EpisodeDto>> getContentEpisodes(@PathVariable UUID id, Pageable pageable) {
        Page<EpisodeDto> episodes = contentService.getEpisodesByContentId(id, pageable);
        return ResponseEntity.ok(episodes);
    }
    
    @GetMapping("/{id}/genres")
    public ResponseEntity<Page<GenreDto>> getContentGenres(@PathVariable UUID id, Pageable pageable) {
        Page<GenreDto> genres = contentService.getGenresByContentId(id, pageable);
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
    public ResponseEntity<Page<ContentDto>> getRecommendations(@RequestParam UUID userId, Pageable pageable) {
        Page<ContentDto> recommendations = contentService.getContentRecommendations(userId, pageable);
        return ResponseEntity.ok(recommendations);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Page<ContentDto>> filterContent(
            @RequestParam(value = "by-type", required = false) String byType,
            @RequestParam(value = "by-genre", required = false) String byGenre,
            Pageable pageable) {
        Page<ContentDto> content = contentService.filterContent(byType, byGenre, pageable);
        return ResponseEntity.ok(content);
    }
}