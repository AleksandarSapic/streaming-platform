package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    
    Page<Content> findByIsAvailable(boolean isAvailable, Pageable pageable);
    
    Page<Content> findByContentTypeId(UUID contentTypeId, Pageable pageable);
    
    @Query("SELECT c FROM Content c WHERE c.contentType.name = :typeName")
    Page<Content> findByContentTypeName(@Param("typeName") String typeName, Pageable pageable);
    
    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Content> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    Page<Content> findByLanguage(String language, Pageable pageable);
    
    Page<Content> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Page<Content> findByReleaseDateAfter(LocalDate date, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Content c JOIN c.contentGenres cg WHERE LOWER(cg.genre.name) = LOWER(:genreName)")
    Page<Content> findByGenreName(@Param("genreName") String genreName, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.isAvailable = true")
    Page<Content> findAvailableContentOrderByReleaseDateDesc(Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Content c WHERE c.contentType.id = :contentTypeId")
    Long countByContentTypeId(@Param("contentTypeId") UUID contentTypeId);
    
    @Query("SELECT DISTINCT c FROM Content c JOIN c.contentGenres cg " +
            "WHERE (:typeName IS NULL OR LOWER(c.contentType.name) = LOWER(:typeName)) " +
            "AND (:genreName IS NULL OR LOWER(cg.genre.name) = LOWER(:genreName))")
    Page<Content> findByTypeAndGenre(@Param("typeName") String typeName, 
                                    @Param("genreName") String genreName, 
                                    Pageable pageable);
}