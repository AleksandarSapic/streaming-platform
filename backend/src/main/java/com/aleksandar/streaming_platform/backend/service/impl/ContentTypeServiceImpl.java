package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.ContentTypeDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.exception.BusinessLogicException;
import com.aleksandar.streaming_platform.backend.exception.DuplicateResourceException;
import com.aleksandar.streaming_platform.backend.exception.ResourceNotFoundException;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.ContentType;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.repository.ContentTypeRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.service.ContentTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContentTypeServiceImpl implements ContentTypeService {
    
    private final ContentTypeRepository contentTypeRepository;
    private final ContentRepository contentRepository;
    private final DtoMapper dtoMapper;
    
    public ContentTypeServiceImpl(ContentTypeRepository contentTypeRepository,
                                 ContentRepository contentRepository,
                                 DtoMapper dtoMapper) {
        this.contentTypeRepository = contentTypeRepository;
        this.contentRepository = contentRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public ContentTypeDto createContentType(ContentTypeDto contentTypeDto) {
        if (contentTypeRepository.existsByName(contentTypeDto.name())) {
            throw new DuplicateResourceException("ContentType", "name", contentTypeDto.name());
        }
        
        ContentType contentType = new ContentType();
        contentType.setName(contentTypeDto.name());
        ContentType savedContentType = contentTypeRepository.save(contentType);
        return dtoMapper.toContentTypeDto(savedContentType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContentTypeDto> getContentTypeById(UUID id) {
        return contentTypeRepository.findById(id)
                .map(dtoMapper::toContentTypeDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContentTypeDto> getContentTypeByName(String name) {
        return contentTypeRepository.findByName(name)
                .map(dtoMapper::toContentTypeDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentTypeDto> getAllContentTypes() {
        List<ContentType> contentTypes = contentTypeRepository.findAll();
        return dtoMapper.toContentTypeDtoList(contentTypes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentTypeDto> getAllContentTypesOrderedByName() {
        List<ContentType> contentTypes = contentTypeRepository.findAllOrderByName();
        return dtoMapper.toContentTypeDtoList(contentTypes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentTypeDto> searchContentTypesByName(String name) {
        List<ContentType> contentTypes = contentTypeRepository.findByNameContainingIgnoreCase(name);
        return dtoMapper.toContentTypeDtoList(contentTypes);
    }
    
    @Override
    public ContentTypeDto updateContentType(ContentTypeDto contentTypeDto) {
        ContentType existingContentType = contentTypeRepository.findById(contentTypeDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("ContentType", "id", contentTypeDto.id()));
        
        if (!existingContentType.getName().equals(contentTypeDto.name()) &&
            contentTypeRepository.existsByName(contentTypeDto.name())) {
            throw new DuplicateResourceException("ContentType", "name", contentTypeDto.name());
        }
        
        existingContentType.setName(contentTypeDto.name());
        ContentType savedContentType = contentTypeRepository.save(existingContentType);
        return dtoMapper.toContentTypeDto(savedContentType);
    }
    
    @Override
    public void deleteContentType(UUID id) {
        if (!contentTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("ContentType", "id", id);
        }
        
        Long contentCount = getContentCountByTypeId(id);
        if (contentCount > 0) {
            throw new BusinessLogicException("Cannot delete content type. " + contentCount + " content items are assigned this type.");
        }
        
        contentTypeRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return contentTypeRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByTypeId(UUID typeId) {
        List<Content> contents = contentRepository.findByContentTypeId(typeId);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getContentByTypeName(String typeName) {
        List<Content> contents = contentRepository.findByContentTypeName(typeName);
        return dtoMapper.toContentDtoList(contents);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getContentCountByTypeId(UUID typeId) {
        return (long) contentRepository.findByContentTypeId(typeId).size();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContentTypeDto> getPopularContentTypes() {
        // TODO: Implement popularity algorithm based on content count, user preferences, etc.
        // For now, return all content types ordered by name
        return getAllContentTypesOrderedByName();
    }
}