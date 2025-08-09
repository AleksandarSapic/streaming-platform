package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Episode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
    
    Page<Episode> findByContentId(UUID contentId, Pageable pageable);
    
    Page<Episode> findByContentIdOrderBySeasonNumberAscEpisodeNumberAsc(UUID contentId, Pageable pageable);
    
    Page<Episode> findByContentIdAndSeasonNumber(UUID contentId, Integer seasonNumber, Pageable pageable);
    
    Optional<Episode> findByContentIdAndSeasonNumberAndEpisodeNumber(UUID contentId, Integer seasonNumber, Integer episodeNumber);
    
    @Query("SELECT DISTINCT e.seasonNumber FROM Episode e WHERE e.content.id = :contentId")
    Page<Integer> findDistinctSeasonNumbersByContentId(@Param("contentId") UUID contentId, Pageable pageable);
    
    @Query("SELECT e FROM Episode e WHERE e.content.id = :contentId AND e.seasonNumber = :seasonNumber ORDER BY e.episodeNumber")
    List<Episode> findEpisodesByContentIdAndSeasonNumberOrderByEpisodeNumber(@Param("contentId") UUID contentId, @Param("seasonNumber") Integer seasonNumber);
    
    @Query("SELECT COUNT(e) FROM Episode e WHERE e.content.id = :contentId")
    Long countByContentId(@Param("contentId") UUID contentId);
    
    @Query("SELECT COUNT(e) FROM Episode e WHERE e.content.id = :contentId AND e.seasonNumber = :seasonNumber")
    Long countByContentIdAndSeasonNumber(@Param("contentId") UUID contentId, @Param("seasonNumber") Integer seasonNumber);
    
    @Query("SELECT DISTINCT e.seasonNumber FROM Episode e WHERE e.content.id = :contentId ORDER BY e.seasonNumber")
    List<Integer> findDistinctSeasonNumbersByContentIdList(@Param("contentId") UUID contentId);
}