package com.aleksandar.streaming_platform.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "content_genres")
@IdClass(ContentGenreId.class)
public class ContentGenre {
    
    @Id
    @Column(name = "content_id")
    private UUID contentId;
    
    @Id
    @Column(name = "genre_id")
    private UUID genreId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Content content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genre genre;
    
    public UUID getContentId() {
        return contentId;
    }
    
    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }
    
    public UUID getGenreId() {
        return genreId;
    }
    
    public void setGenreId(UUID genreId) {
        this.genreId = genreId;
    }
    
    public Content getContent() {
        return content;
    }
    
    public void setContent(Content content) {
        this.content = content;
    }
    
    public Genre getGenre() {
        return genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}