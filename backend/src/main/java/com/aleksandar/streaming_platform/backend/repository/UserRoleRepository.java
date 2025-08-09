package com.aleksandar.streaming_platform.backend.repository;

import com.aleksandar.streaming_platform.backend.model.UserRole;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    Optional<UserRole> findByName(UserRoleType name);
    
    boolean existsByName(UserRoleType name);
}