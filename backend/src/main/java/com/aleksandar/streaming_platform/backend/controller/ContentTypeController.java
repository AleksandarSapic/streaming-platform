package com.aleksandar.streaming_platform.backend.controller;

import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.dto.ContentTypeDto;
import com.aleksandar.streaming_platform.backend.service.ContentTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content-types")
public class ContentTypeController {
    
    private final ContentTypeService contentTypeService;
    
    public ContentTypeController(ContentTypeService contentTypeService) {
        this.contentTypeService = contentTypeService;
    }
    
    @PostMapping
    public ResponseEntity<ContentTypeDto> createContentType(@Valid @RequestBody ContentTypeDto contentTypeDto) {
        ContentTypeDto contentType = contentTypeService.createContentType(contentTypeDto);
        return new ResponseEntity<>(contentType, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentTypeDto> getContentTypeById(@PathVariable UUID id) {
        return contentTypeService.getContentTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ContentTypeDto> getContentTypeByName(@PathVariable String name) {
        return contentTypeService.getContentTypeByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<ContentTypeDto>> getAllContentTypes(Pageable pageable) {
        Page<ContentTypeDto> contentTypes = contentTypeService.getAllContentTypesOrderedByName(pageable);
        return ResponseEntity.ok(contentTypes);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ContentTypeDto>> searchContentTypes(@RequestParam String name, Pageable pageable) {
        Page<ContentTypeDto> contentTypes = contentTypeService.searchContentTypesByName(name, pageable);
        return ResponseEntity.ok(contentTypes);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<Page<ContentTypeDto>> getPopularContentTypes(Pageable pageable) {
        Page<ContentTypeDto> contentTypes = contentTypeService.getPopularContentTypes(pageable);
        return ResponseEntity.ok(contentTypes);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContentTypeDto> updateContentType(@PathVariable UUID id, @Valid @RequestBody ContentTypeDto contentTypeDto) {
        if (!id.equals(contentTypeDto.id())) {
            return ResponseEntity.badRequest().build();
        }
        ContentTypeDto updatedContentType = contentTypeService.updateContentType(contentTypeDto);
        return ResponseEntity.ok(updatedContentType);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContentType(@PathVariable UUID id) {
        contentTypeService.deleteContentType(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/content")
    public ResponseEntity<Page<ContentDto>> getContentByType(@PathVariable UUID id, Pageable pageable) {
        Page<ContentDto> content = contentTypeService.getContentByTypeId(id, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/by-name/{name}/content")
    public ResponseEntity<Page<ContentDto>> getContentByTypeName(@PathVariable String name, Pageable pageable) {
        Page<ContentDto> content = contentTypeService.getContentByTypeName(name, pageable);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}/content/count")
    public ResponseEntity<Long> getContentCountByType(@PathVariable UUID id) {
        Long count = contentTypeService.getContentCountByTypeId(id);
        return ResponseEntity.ok(count);
    }
}