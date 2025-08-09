package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Watchlist;
import com.aleksandar.streaming_platform.backend.model.WatchlistId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, WatchlistId> {
    
    Page<Watchlist> findByUserId(UUID userId, Pageable pageable);
    
    Page<Watchlist> findByContentId(UUID contentId, Pageable pageable);
    
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);
    
    void deleteByUserIdAndContentId(UUID userId, UUID contentId);
    
    @Query("SELECT w FROM Watchlist w WHERE w.userId = :userId")
    Page<Watchlist> findByUserIdOrderByAddedAtDesc(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT COUNT(w) FROM Watchlist w WHERE w.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(w) FROM Watchlist w WHERE w.contentId = :contentId")
    Long countByContentId(@Param("contentId") UUID contentId);
    
    @Query("SELECT w FROM Watchlist w WHERE w.userId = :userId")
    List<Watchlist> findByUserIdList(@Param("userId") UUID userId);
    
    @Query("SELECT w FROM Watchlist w WHERE w.userId = :userId AND EXISTS (SELECT cg FROM ContentGenre cg WHERE cg.contentId = w.contentId AND cg.genre.name = :genreName)")
    Page<Watchlist> findByUserIdAndContentGenreName(@Param("userId") UUID userId, @Param("genreName") String genreName, Pageable pageable);
    
    @Query("SELECT w FROM Watchlist w WHERE w.userId = :userId AND EXISTS (SELECT c FROM Content c WHERE c.id = w.contentId AND c.contentType.name = :typeName)")
    Page<Watchlist> findByUserIdAndContentTypeName(@Param("userId") UUID userId, @Param("typeName") String typeName, Pageable pageable);
}