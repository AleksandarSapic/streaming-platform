package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Watchlist;
import com.aleksandar.streaming_platform.backend.model.WatchlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, WatchlistId> {
    
    List<Watchlist> findByUserId(UUID userId);
    
    List<Watchlist> findByContentId(UUID contentId);
    
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);
    
    void deleteByUserIdAndContentId(UUID userId, UUID contentId);
    
    @Query("SELECT w FROM Watchlist w WHERE w.userId = :userId ORDER BY w.addedAt DESC")
    List<Watchlist> findByUserIdOrderByAddedAtDesc(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(w) FROM Watchlist w WHERE w.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT COUNT(w) FROM Watchlist w WHERE w.contentId = :contentId")
    Long countByContentId(@Param("contentId") UUID contentId);
}