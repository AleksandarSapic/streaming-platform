package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.GenreDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {
    
    private final GenreService genreService;
    
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }
    
    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@Valid @RequestBody GenreDto genreDto) {
        GenreDto genre = genreService.createGenre(genreDto);
        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable UUID id) {
        return genreService.getGenreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<GenreDto> getGenreByName(@PathVariable String name) {
        return genreService.getGenreByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<GenreDto>> getAllGenres(Pageable pageable) {
        Page<GenreDto> genres = genreService.getAllGenresOrderedByName(pageable);
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<GenreDto>> searchGenres(@RequestParam String name, Pageable pageable) {
        Page<GenreDto> genres = genreService.searchGenresByName(name, pageable);
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<Page<GenreDto>> getPopularGenres(Pageable pageable) {
        Page<GenreDto> genres = genreService.getPopularGenres(pageable);
        return ResponseEntity.ok(genres);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable UUID id, @Valid @RequestBody GenreDto genreDto) {
        if (!id.equals(genreDto.id())) {
            return ResponseEntity.badRequest().build();
        }
        GenreDto updatedGenre = genreService.updateGenre(genreDto);
        return ResponseEntity.ok(updatedGenre);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/content")
    public ResponseEntity<Page<ContentDto>> getContentByGenre(@PathVariable UUID id, Pageable pageable) {
        Page<ContentDto> content = genreService.getContentByGenreId(id, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-name/{name}/content")
    public ResponseEntity<Page<ContentDto>> getContentByGenreName(@PathVariable String name, Pageable pageable) {
        Page<ContentDto> content = genreService.getContentByGenreName(name, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}/content/count")
    public ResponseEntity<Long> getContentCountByGenre(@PathVariable UUID id) {
        Long count = genreService.getContentCountByGenreId(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/by-content/{contentId}")
    public ResponseEntity<Page<GenreDto>> getGenresByContent(@PathVariable UUID contentId, Pageable pageable) {
        Page<GenreDto> genres = genreService.getGenresByContentId(contentId, pageable);
        return ResponseEntity.ok(genres);
    }
}