package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentTypeRepository extends JpaRepository<ContentType, UUID> {
    
    Optional<ContentType> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT ct FROM ContentType ct WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ContentType> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT ct FROM ContentType ct")
    Page<ContentType> findAllOrderByName(Pageable pageable);
}