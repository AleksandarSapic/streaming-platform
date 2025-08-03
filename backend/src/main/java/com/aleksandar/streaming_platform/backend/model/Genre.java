package com.aleksandar.streaming_platform.backend.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "genres")
public class Genre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    private List<ContentGenre> contentGenres;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<ContentGenre> getContentGenres() {
        return contentGenres;
    }
    
    public void setContentGenres(List<ContentGenre> contentGenres) {
        this.contentGenres = contentGenres;
    }
}