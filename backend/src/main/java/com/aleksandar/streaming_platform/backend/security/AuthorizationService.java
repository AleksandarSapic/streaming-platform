package com.aleksandar.streaming_platform.backend.security;

import com.aleksandar.streaming_platform.backend.repository.WatchlistRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService {

    private final WatchlistRepository watchlistRepository;

    public AuthorizationService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    /**
     * Validates that the current user can access/modify the specified user resource
     * Allows access if:
     * 1. Current user is the same as target user (self-access)
     * 2. Current user is an admin
     */
    public void validateUserAccess(UUID targetUserId) {
        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (SecurityUtils.isAdmin()) {
            return;
        }

        if (!currentUserId.equals(targetUserId)) {
            throw new AccessDeniedException("Access denied: cannot access another user's resources");
        }
    }

    /**
     * Validates that the current user can access/modify watchlist resources for the specified user
     */
    public void validateWatchlistAccess(UUID targetUserId) {
        validateUserAccess(targetUserId);
    }

    /**
     * Validates that only admins can perform admin-only operations
     */
    public void validateAdminAccess() {
        if (!SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Access denied: admin privileges required");
        }
    }

    /**
     * Validates that the current user can assign roles
     * Only admins can assign roles
     */
    public void validateRoleAssignmentAccess() {
        validateAdminAccess();
    }

    /**
     * Gets the current user's ID from the security context
     */
    public UUID getCurrentUserId() {
        UUID currentUserId = SecurityUtils.getCurrentUserIdAsUUID();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return currentUserId;
    }

    /**
     * Validates that the current user is the owner of the specified watchlist entry
     */
    public void validateWatchlistOwnership(UUID userId, UUID contentId) {
        validateUserAccess(userId);
        
        if (!watchlistRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new AccessDeniedException("Watchlist entry not found or access denied");
        }
    }
}