package com.aleksandar.streaming_platform.backend.security;

import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Gets the user ID of the currently authenticated user
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        // For JWT authentication, the name contains the user ID from JWT subject
        if (authentication instanceof JwtAuthenticationToken) {
            return authentication.getName();
        }
        
        return authentication.getName();
    }

    /**
     * Gets the user ID of the currently authenticated user as UUID
     */
    public static UUID getCurrentUserIdAsUUID() {
        String userIdStr = getCurrentUserId();
        try {
            return userIdStr != null ? UUID.fromString(userIdStr) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Gets the authorities/roles of the currently authenticated user
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getAuthorities();
    }

    /**
     * Checks if the current user has a specific role
     */
    public static boolean hasRole(UserRoleType roleType) {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserAuthorities();
        if (authorities == null) {
            return false;
        }
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleType.getRoleName()));
    }

    /**
     * Checks if the current user has a specific role (string version for backward compatibility)
     */
    public static boolean hasRole(String role) {
        try {
            UserRoleType roleType = UserRoleType.fromString(role);
            return hasRole(roleType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if the current user is an admin
     */
    public static boolean isAdmin() {
        return hasRole(UserRoleType.ADMIN);
    }
    
    /**
     * Gets the current user's role as enum
     */
    public static UserRoleType getCurrentUserRoleType() {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return UserRoleType.USER;
        }
        
        String roleString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.substring(5)) // Remove "ROLE_" prefix
                .findFirst()
                .orElse("USER");
        
        try {
            return UserRoleType.fromString(roleString);
        } catch (IllegalArgumentException e) {
            return UserRoleType.USER;
        }
    }

    /**
     * Gets the current user's role name from their authorities (string version for backward compatibility)
     */
    public static String getCurrentUserRole() {
        return getCurrentUserRoleType().getRoleName();
    }

    /**
     * Checks if the current user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}