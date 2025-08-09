package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByCountry(String country);
    
    @Query("SELECT u FROM User u WHERE u.userRole.name = :roleType")
    List<User> findByUserRoleType(@Param("roleType") UserRoleType roleType);
    
    @Query("SELECT u FROM User u WHERE u.userRole.name = :roleName")
    List<User> findByUserRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRole WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") UUID id);
}