package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentTypeRepository extends JpaRepository<ContentType, UUID> {
    
    Optional<ContentType> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT ct FROM ContentType ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ContentType> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT ct FROM ContentType ct ORDER BY ct.name")
    List<ContentType> findAllOrderByName();
}