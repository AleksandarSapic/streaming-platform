package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Page<User> findByCountry(String country, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.userRole.name = :roleType")
    Page<User> findByUserRoleType(@Param("roleType") UserRoleType roleType, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.userRole.name = :roleName")
    Page<User> findByUserRoleName(@Param("roleName") String roleName, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRole WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") UUID id);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userRole.name = :roleType")
    Long countByUserRoleType(@Param("roleType") UserRoleType roleType);
}