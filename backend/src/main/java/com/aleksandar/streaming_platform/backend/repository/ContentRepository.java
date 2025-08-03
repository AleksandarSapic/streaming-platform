package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    
    List<Content> findByIsAvailable(boolean isAvailable);
    
    List<Content> findByContentTypeId(UUID contentTypeId);
    
    @Query("SELECT c FROM Content c WHERE c.contentType.name = :typeName")
    List<Content> findByContentTypeName(@Param("typeName") String typeName);
    
    @Query("SELECT c FROM Content c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Content> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    List<Content> findByLanguage(String language);
    
    List<Content> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Content> findByReleaseDateAfter(LocalDate date);
    
    @Query("SELECT DISTINCT c FROM Content c JOIN c.contentGenres cg WHERE cg.genre.name = :genreName")
    List<Content> findByGenreName(@Param("genreName") String genreName);
    
    @Query("SELECT c FROM Content c WHERE c.isAvailable = true ORDER BY c.releaseDate DESC")
    List<Content> findAvailableContentOrderByReleaseDateDesc();
}