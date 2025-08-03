package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    
    Optional<Genre> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT g FROM Genre g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Genre> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT g FROM Genre g ORDER BY g.name")
    List<Genre> findAllOrderByName();
}