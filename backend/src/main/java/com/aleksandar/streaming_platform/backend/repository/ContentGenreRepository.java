package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.ContentGenre;
import com.aleksandar.streaming_platform.backend.model.ContentGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentGenreRepository extends JpaRepository<ContentGenre, ContentGenreId> {
    
    List<ContentGenre> findByContentId(UUID contentId);
    
    List<ContentGenre> findByGenreId(UUID genreId);
    
    boolean existsByContentIdAndGenreId(UUID contentId, UUID genreId);
    
    void deleteByContentIdAndGenreId(UUID contentId, UUID genreId);
    
    @Query("SELECT cg FROM ContentGenre cg WHERE cg.content.title LIKE %:title%")
    List<ContentGenre> findByContentTitleContaining(@Param("title") String title);
    
    @Query("SELECT cg FROM ContentGenre cg WHERE cg.genre.name = :genreName")
    List<ContentGenre> findByGenreName(@Param("genreName") String genreName);
    
    @Query("SELECT COUNT(cg) FROM ContentGenre cg WHERE cg.contentId = :contentId")
    Long countByContentId(@Param("contentId") UUID contentId);
    
    @Query("SELECT COUNT(cg) FROM ContentGenre cg WHERE cg.genreId = :genreId")
    Long countByGenreId(@Param("genreId") UUID genreId);
}