package com.aleksandar.streaming_platform.backend.service;

import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.CreateEpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EpisodeService {
    
    EpisodeDto createEpisode(CreateEpisodeDto createEpisodeDto);
    
    Optional<EpisodeDto> getEpisodeById(UUID id);
    
    Page<EpisodeDto> getAllEpisodes(Pageable pageable);
    
    Page<EpisodeDto> getEpisodesByContentId(UUID contentId, Pageable pageable);
    
    Page<EpisodeDto> getEpisodesByContentIdOrderedBySeasonAndEpisode(UUID contentId, Pageable pageable);
    
    Page<EpisodeDto> getEpisodesByContentIdAndSeason(UUID contentId, Integer seasonNumber, Pageable pageable);
    
    Optional<EpisodeDto> getSpecificEpisode(UUID contentId, Integer seasonNumber, Integer episodeNumber);
    
    EpisodeDto updateEpisode(EpisodeDto episodeDto);
    
    void deleteEpisode(UUID id);
    
    List<Integer> getSeasonNumbersByContentId(UUID contentId);
    
    Long getTotalEpisodeCountByContentId(UUID contentId);
    
    Long getEpisodeCountByContentIdAndSeason(UUID contentId, Integer seasonNumber);
    
    EpisodeDto getNextEpisode(UUID contentId, Integer currentSeason, Integer currentEpisode);
    
    EpisodeDto getPreviousEpisode(UUID contentId, Integer currentSeason, Integer currentEpisode);
    
    ContentDto getContentByEpisodeId(UUID episodeId);
    
    boolean isValidEpisodeNumber(UUID contentId, Integer seasonNumber, Integer episodeNumber);
}