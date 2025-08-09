package com.aleksandar.streaming_platform.backend.security;

import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret.key:}")
    private String secretKey;
    
    @Value("${jwt.expected.issuer:}")
    private String expectedIssuer;
    
    @Value("${jwt.leeway:60}")
    private long clockSkewSeconds;
    
    @Bean
    public JwtDecoder jwtDecoder() {
        // For symmetric key (HMAC) - development/internal use
        if (!secretKey.isEmpty()) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(), 
                JWSAlgorithm.HS256.getName()
            );
            
            NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
            
            // Add custom validators
            if (!expectedIssuer.isEmpty()) {
                decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(expectedIssuer));
            }
            // Note: Clock skew is handled by the validator, not set directly on decoder
            
            return decoder;
        }
        
        // For asymmetric keys (RSA/ECDSA) - production use
        // This would typically use JWK Set URI from authorization server
        // Uncomment and configure for production:
        // return NimbusJwtDecoder.withJwkSetUri("https://your-auth-server/.well-known/jwks.json").build();
        
        throw new IllegalStateException("JWT configuration is missing. Please configure either jwt.secret.key for development or JWK Set URI for production.");
    }
}