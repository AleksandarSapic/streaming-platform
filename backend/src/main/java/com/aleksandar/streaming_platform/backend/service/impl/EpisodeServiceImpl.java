package com.aleksandar.streaming_platform.backend.service.impl;

import com.aleksandar.streaming_platform.backend.dto.EpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.CreateEpisodeDto;
import com.aleksandar.streaming_platform.backend.dto.ContentDto;
import com.aleksandar.streaming_platform.backend.mapper.DtoMapper;
import com.aleksandar.streaming_platform.backend.model.Episode;
import com.aleksandar.streaming_platform.backend.model.Content;
import com.aleksandar.streaming_platform.backend.repository.EpisodeRepository;
import com.aleksandar.streaming_platform.backend.repository.ContentRepository;
import com.aleksandar.streaming_platform.backend.service.EpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EpisodeServiceImpl implements EpisodeService {
    
    private final EpisodeRepository episodeRepository;
    private final ContentRepository contentRepository;
    private final DtoMapper dtoMapper;
    
    public EpisodeServiceImpl(EpisodeRepository episodeRepository,
                             ContentRepository contentRepository,
                             DtoMapper dtoMapper) {
        this.episodeRepository = episodeRepository;
        this.contentRepository = contentRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public EpisodeDto createEpisode(CreateEpisodeDto createEpisodeDto) {
        // Validate that content exists
        Content content = contentRepository.findById(createEpisodeDto.contentId())
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        // Check if episode already exists
        Optional<Episode> existingEpisode = episodeRepository.findByContentIdAndSeasonNumberAndEpisodeNumber(
                createEpisodeDto.contentId(), 
                createEpisodeDto.seasonNumber(), 
                createEpisodeDto.episodeNumber());
        
        if (existingEpisode.isPresent()) {
            throw new RuntimeException("Episode already exists for this season and episode number");
        }
        
        Episode episode = dtoMapper.toEpisodeEntity(createEpisodeDto);
        episode.setContent(content);
        
        Episode savedEpisode = episodeRepository.save(episode);
        return dtoMapper.toEpisodeDto(savedEpisode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<EpisodeDto> getEpisodeById(UUID id) {
        return episodeRepository.findById(id)
                .map(dtoMapper::toEpisodeDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDto> getAllEpisodes() {
        List<Episode> episodes = episodeRepository.findAll();
        return dtoMapper.toEpisodeDtoList(episodes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDto> getEpisodesByContentId(UUID contentId) {
        List<Episode> episodes = episodeRepository.findByContentId(contentId);
        return dtoMapper.toEpisodeDtoList(episodes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDto> getEpisodesByContentIdOrderedBySeasonAndEpisode(UUID contentId) {
        List<Episode> episodes = episodeRepository.findByContentIdOrderBySeasonNumberAscEpisodeNumberAsc(contentId);
        return dtoMapper.toEpisodeDtoList(episodes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDto> getEpisodesByContentIdAndSeason(UUID contentId, Integer seasonNumber) {
        List<Episode> episodes = episodeRepository.findByContentIdAndSeasonNumber(contentId, seasonNumber);
        return dtoMapper.toEpisodeDtoList(episodes);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<EpisodeDto> getSpecificEpisode(UUID contentId, Integer seasonNumber, Integer episodeNumber) {
        return episodeRepository.findByContentIdAndSeasonNumberAndEpisodeNumber(contentId, seasonNumber, episodeNumber)
                .map(dtoMapper::toEpisodeDto);
    }
    
    @Override
    public EpisodeDto updateEpisode(EpisodeDto episodeDto) {
        Episode existingEpisode = episodeRepository.findById(episodeDto.id())
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        
        // Check if the new season/episode number conflicts with existing episodes
        if (!existingEpisode.getSeasonNumber().equals(episodeDto.seasonNumber()) || 
            !existingEpisode.getEpisodeNumber().equals(episodeDto.episodeNumber())) {
            
            Optional<Episode> conflictingEpisode = episodeRepository.findByContentIdAndSeasonNumberAndEpisodeNumber(
                    existingEpisode.getContent().getId(), 
                    episodeDto.seasonNumber(), 
                    episodeDto.episodeNumber());
            
            if (conflictingEpisode.isPresent()) {
                throw new RuntimeException("Episode already exists for this season and episode number");
            }
        }
        
        existingEpisode.setSeasonNumber(episodeDto.seasonNumber());
        existingEpisode.setEpisodeNumber(episodeDto.episodeNumber());
        existingEpisode.setTitle(episodeDto.title());
        existingEpisode.setDescription(episodeDto.description());
        existingEpisode.setDuration(episodeDto.duration());
        existingEpisode.setReleaseDate(episodeDto.releaseDate());
        existingEpisode.setThumbnailUrl(episodeDto.thumbnailUrl());
        existingEpisode.setVideoUrl(episodeDto.videoUrl());
        
        Episode savedEpisode = episodeRepository.save(existingEpisode);
        return dtoMapper.toEpisodeDto(savedEpisode);
    }
    
    @Override
    public void deleteEpisode(UUID id) {
        if (!episodeRepository.existsById(id)) {
            throw new RuntimeException("Episode not found");
        }
        episodeRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSeasonNumbersByContentId(UUID contentId) {
        return episodeRepository.findDistinctSeasonNumbersByContentId(contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getTotalEpisodeCountByContentId(UUID contentId) {
        return episodeRepository.countByContentId(contentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getEpisodeCountByContentIdAndSeason(UUID contentId, Integer seasonNumber) {
        return episodeRepository.countByContentIdAndSeasonNumber(contentId, seasonNumber);
    }
    
    @Override
    @Transactional(readOnly = true)
    public EpisodeDto getNextEpisode(UUID contentId, Integer currentSeason, Integer currentEpisode) {
        // First try to find next episode in same season
        List<Episode> sameSeason = episodeRepository.findEpisodesByContentIdAndSeasonNumberOrderByEpisodeNumber(
                contentId, currentSeason);
        
        for (Episode episode : sameSeason) {
            if (episode.getEpisodeNumber() > currentEpisode) {
                return dtoMapper.toEpisodeDto(episode);
            }
        }
        
        // If no next episode in same season, find first episode of next season
        List<Integer> seasons = getSeasonNumbersByContentId(contentId);
        for (Integer season : seasons) {
            if (season > currentSeason) {
                List<Episode> nextSeason = episodeRepository.findEpisodesByContentIdAndSeasonNumberOrderByEpisodeNumber(
                        contentId, season);
                if (!nextSeason.isEmpty()) {
                    return dtoMapper.toEpisodeDto(nextSeason.get(0));
                }
            }
        }
        
        return null; // No next episode found
    }
    
    @Override
    @Transactional(readOnly = true)
    public EpisodeDto getPreviousEpisode(UUID contentId, Integer currentSeason, Integer currentEpisode) {
        // First try to find previous episode in same season
        List<Episode> sameSeason = episodeRepository.findEpisodesByContentIdAndSeasonNumberOrderByEpisodeNumber(
                contentId, currentSeason);
        
        Episode previousEpisode = null;
        for (Episode episode : sameSeason) {
            if (episode.getEpisodeNumber() >= currentEpisode) {
                break;
            }
            previousEpisode = episode;
        }
        
        if (previousEpisode != null) {
            return dtoMapper.toEpisodeDto(previousEpisode);
        }
        
        // If no previous episode in same season, find last episode of previous season
        List<Integer> seasons = getSeasonNumbersByContentId(contentId);
        Integer prevSeason = null;
        for (Integer season : seasons) {
            if (season >= currentSeason) {
                break;
            }
            prevSeason = season;
        }
        
        if (prevSeason != null) {
            List<Episode> prevSeasonEpisodes = episodeRepository.findEpisodesByContentIdAndSeasonNumberOrderByEpisodeNumber(
                    contentId, prevSeason);
            if (!prevSeasonEpisodes.isEmpty()) {
                return dtoMapper.toEpisodeDto(prevSeasonEpisodes.get(prevSeasonEpisodes.size() - 1));
            }
        }
        
        return null; // No previous episode found
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContentDto getContentByEpisodeId(UUID episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));
        
        return dtoMapper.toContentDto(episode.getContent());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isValidEpisodeNumber(UUID contentId, Integer seasonNumber, Integer episodeNumber) {
        return episodeRepository.findByContentIdAndSeasonNumberAndEpisodeNumber(
                contentId, seasonNumber, episodeNumber).isPresent();
    }
}