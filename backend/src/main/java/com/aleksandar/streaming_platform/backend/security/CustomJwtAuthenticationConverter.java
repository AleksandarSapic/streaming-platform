package com.aleksandar.streaming_platform.backend.security;

import com.aleksandar.streaming_platform.backend.model.User;
import com.aleksandar.streaming_platform.backend.model.UserRoleType;
import com.aleksandar.streaming_platform.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomJwtAuthenticationConverter.class);
    
    private final UserRepository userRepository;
    
    @Value("${jwt.expected.issuer}")
    private String expectedIssuer;
    
    @Value("${jwt.expected.audience}")
    private String expectedAudience;
    
    public CustomJwtAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        try {
            // Comprehensive JWT validation
            validateJwtClaims(jwt);
            
            // Extract user ID from subject claim
            String userId = jwt.getSubject();
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT subject claim is missing or empty");
            }
            
            // Validate UUID format
            UUID userUuid;
            try {
                userUuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format in JWT subject: " + userId);
            }
            
            // Fetch user authorities from database
            List<SimpleGrantedAuthority> authorities = getUserAuthorities(userUuid);
            
            logger.debug("Successfully authenticated user: {} with authorities: {}", userId, authorities);
            
            // Create authentication token
            return new JwtAuthenticationToken(jwt, authorities, userId);
            
        } catch (Exception e) {
            logger.error("JWT authentication conversion failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
    
    private void validateJwtClaims(Jwt jwt) {
        // Validate issuer claim
        String issuer = jwt.getClaimAsString("iss");
        if (!expectedIssuer.equals(issuer)) {
            throw new IllegalArgumentException(
                String.format("Invalid issuer claim. Expected: %s, Actual: %s", expectedIssuer, issuer)
            );
        }
        
        // Validate audience claim
        List<String> audiences = jwt.getAudience();
        if (audiences == null || !audiences.contains(expectedAudience)) {
            throw new IllegalArgumentException(
                String.format("Invalid audience claim. Expected: %s, Actual: %s", expectedAudience, audiences)
            );
        }
        
        // Validate expiration (additional check)
        Instant expiresAt = jwt.getExpiresAt();
        if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("JWT token has expired");
        }
        
        // Validate not before claim if present
        Instant notBefore = jwt.getNotBefore();
        if (notBefore != null && notBefore.isAfter(Instant.now())) {
            throw new IllegalArgumentException("JWT token is not yet valid");
        }
    }
    
    private List<SimpleGrantedAuthority> getUserAuthorities(UUID userId) {
        try {
            Optional<User> userOpt = userRepository.findByIdWithRole(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserRoleType roleType = user.getUserRole() != null ? user.getUserRole().getName() : UserRoleType.USER;
                
                logger.debug("User {} has role: {}", userId, roleType);
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleType.getRoleName()));
            } else {
                logger.warn("User not found in database: {}", userId);
                return Collections.emptyList();
            }
            
        } catch (Exception e) {
            logger.error("Error fetching user authorities for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }
}