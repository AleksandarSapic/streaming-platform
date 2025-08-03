package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.ContentTypeDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentTypeService {
    
    ContentTypeDto createContentType(ContentTypeDto contentTypeDto);
    
    Optional<ContentTypeDto> getContentTypeById(UUID id);
    
    Optional<ContentTypeDto> getContentTypeByName(String name);
    
    List<ContentTypeDto> getAllContentTypes();
    
    List<ContentTypeDto> getAllContentTypesOrderedByName();
    
    List<ContentTypeDto> searchContentTypesByName(String name);
    
    ContentTypeDto updateContentType(ContentTypeDto contentTypeDto);
    
    void deleteContentType(UUID id);
    
    boolean existsByName(String name);
    
    List<ContentDto> getContentByTypeId(UUID typeId);
    
    List<ContentDto> getContentByTypeName(String typeName);
    
    Long getContentCountByTypeId(UUID typeId);
    
    List<ContentTypeDto> getPopularContentTypes();
}