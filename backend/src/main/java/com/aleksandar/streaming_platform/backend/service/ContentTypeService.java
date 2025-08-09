package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentTypeDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ContentTypeService {
    
    ContentTypeDto createContentType(ContentTypeDto contentTypeDto);
    
    Optional<ContentTypeDto> getContentTypeById(UUID id);
    
    Optional<ContentTypeDto> getContentTypeByName(String name);
    
    Page<ContentTypeDto> getAllContentTypes(Pageable pageable);
    
    Page<ContentTypeDto> getAllContentTypesOrderedByName(Pageable pageable);
    
    Page<ContentTypeDto> searchContentTypesByName(String name, Pageable pageable);
    
    ContentTypeDto updateContentType(ContentTypeDto contentTypeDto);
    
    void deleteContentType(UUID id);
    
    boolean existsByName(String name);
    
    Page<ContentDto> getContentByTypeId(UUID typeId, Pageable pageable);
    
    Page<ContentDto> getContentByTypeName(String typeName, Pageable pageable);
    
    Long getContentCountByTypeId(UUID typeId);
    
    Page<ContentTypeDto> getPopularContentTypes(Pageable pageable);
}