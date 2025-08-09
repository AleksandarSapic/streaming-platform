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

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<ContentTypeDto> getAllContentTypes(Pageable pageable) {
        Page<ContentType> contentTypes = contentTypeRepository.findAll(pageable);
        return contentTypes.map(dtoMapper::toContentTypeDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentTypeDto> getAllContentTypesOrderedByName(Pageable pageable) {
        Page<ContentType> contentTypes = contentTypeRepository.findAllOrderByName(pageable);
        return contentTypes.map(dtoMapper::toContentTypeDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentTypeDto> searchContentTypesByName(String name, Pageable pageable) {
        Page<ContentType> contentTypes = contentTypeRepository.findByNameContainingIgnoreCase(name, pageable);
        return contentTypes.map(dtoMapper::toContentTypeDto);
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
    public Page<ContentDto> getContentByTypeId(UUID typeId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByContentTypeId(typeId, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentDto> getContentByTypeName(String typeName, Pageable pageable) {
        Page<Content> contents = contentRepository.findByContentTypeName(typeName, pageable);
        return contents.map(dtoMapper::toContentDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getContentCountByTypeId(UUID typeId) {
        return contentRepository.countByContentTypeId(typeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContentTypeDto> getPopularContentTypes(Pageable pageable) {
        // TODO: Implement popularity algorithm based on content count, user preferences, etc.
        // For now, return all content types ordered by name
        return getAllContentTypesOrderedByName(pageable);
    }
}