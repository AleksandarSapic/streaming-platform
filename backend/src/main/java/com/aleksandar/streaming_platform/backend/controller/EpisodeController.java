package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.CreateEpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.service.EpisodeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/episodes")
public class EpisodeController {
    
    private final EpisodeService episodeService;
    
    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }
    
    @PostMapping
    public ResponseEntity<EpisodeDto> createEpisode(@Valid @RequestBody CreateEpisodeDto createEpisodeDto) {
        EpisodeDto episode = episodeService.createEpisode(createEpisodeDto);
        return new ResponseEntity<>(episode, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EpisodeDto> getEpisodeById(@PathVariable UUID id) {
        return episodeService.getEpisodeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<EpisodeDto>> getAllEpisodes(Pageable pageable) {
        Page<EpisodeDto> episodes = episodeService.getAllEpisodes(pageable);
        return ResponseEntity.ok(episodes);
    }
    
    @GetMapping("/by-content/{contentId}")
    public ResponseEntity<Page<EpisodeDto>> getEpisodesByContent(@PathVariable UUID contentId, Pageable pageable) {
        Page<EpisodeDto> episodes = episodeService.getEpisodesByContentIdOrderedBySeasonAndEpisode(contentId, pageable);
        return ResponseEntity.ok(episodes);
    }
    
    @GetMapping("/by-content/{contentId}/season/{seasonNumber}")
    public ResponseEntity<Page<EpisodeDto>> getEpisodesByContentAndSeason(
            @PathVariable UUID contentId, 
            @PathVariable Integer seasonNumber,
            Pageable pageable) {
        Page<EpisodeDto> episodes = episodeService.getEpisodesByContentIdAndSeason(contentId, seasonNumber, pageable);
        return ResponseEntity.ok(episodes);
    }
    
    @GetMapping("/by-content/{contentId}/season/{seasonNumber}/episode/{episodeNumber}")
    public ResponseEntity<EpisodeDto> getSpecificEpisode(
            @PathVariable UUID contentId,
            @PathVariable Integer seasonNumber,
            @PathVariable Integer episodeNumber) {
        return episodeService.getSpecificEpisode(contentId, seasonNumber, episodeNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EpisodeDto> updateEpisode(@PathVariable UUID id, @Valid @RequestBody EpisodeDto episodeDto) {
        if (!id.equals(episodeDto.id())) {
            return ResponseEntity.badRequest().build();
        }
        EpisodeDto updatedEpisode = episodeService.updateEpisode(episodeDto);
        return ResponseEntity.ok(updatedEpisode);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable UUID id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/by-content/{contentId}/seasons")
    public ResponseEntity<List<Integer>> getSeasonNumbers(@PathVariable UUID contentId) {
        List<Integer> seasons = episodeService.getSeasonNumbersByContentId(contentId);
        return ResponseEntity.ok(seasons);
    }
    
    @GetMapping("/by-content/{contentId}/count")
    public ResponseEntity<Long> getTotalEpisodeCount(@PathVariable UUID contentId) {
        Long count = episodeService.getTotalEpisodeCountByContentId(contentId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/by-content/{contentId}/season/{seasonNumber}/count")
    public ResponseEntity<Long> getSeasonEpisodeCount(
            @PathVariable UUID contentId, 
            @PathVariable Integer seasonNumber) {
        Long count = episodeService.getEpisodeCountByContentIdAndSeason(contentId, seasonNumber);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{contentId}/season/{seasonNumber}/episode/{episodeNumber}/next")
    public ResponseEntity<EpisodeDto> getNextEpisode(
            @PathVariable UUID contentId,
            @PathVariable Integer seasonNumber,
            @PathVariable Integer episodeNumber) {
        EpisodeDto nextEpisode = episodeService.getNextEpisode(contentId, seasonNumber, episodeNumber);
        if (nextEpisode != null) {
            return ResponseEntity.ok(nextEpisode);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{contentId}/season/{seasonNumber}/episode/{episodeNumber}/previous")
    public ResponseEntity<EpisodeDto> getPreviousEpisode(
            @PathVariable UUID contentId,
            @PathVariable Integer seasonNumber,
            @PathVariable Integer episodeNumber) {
        EpisodeDto previousEpisode = episodeService.getPreviousEpisode(contentId, seasonNumber, episodeNumber);
        if (previousEpisode != null) {
            return ResponseEntity.ok(previousEpisode);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/content")
    public ResponseEntity<ContentDto> getContentByEpisode(@PathVariable UUID id) {
        ContentDto content = episodeService.getContentByEpisodeId(id);
        if (content != null) {
            return ResponseEntity.ok(content);
        }
        return ResponseEntity.notFound().build();
    }
}