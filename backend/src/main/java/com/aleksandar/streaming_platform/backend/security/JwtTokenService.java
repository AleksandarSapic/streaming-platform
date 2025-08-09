package com.aleksandar.streaming_platform.backend.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {
    
    @Value("${jwt.secret.key}")
    private String secretKey;
    
    @Value("${jwt.expected.issuer}")
    private String issuer;
    
    @Value("${jwt.expected.audience}")
    private String audience;
    
    private static final long TOKEN_VALIDITY_HOURS = 24;
    
    public String generateToken(UUID userId) {
        try {
            // Create HMAC signer
            JWSSigner signer = new MACSigner(secretKey.getBytes());
            
            // Create JWT claims
            Instant now = Instant.now();
            Instant expiry = now.plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS);
            
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry))
                    .build();
            
            // Create JWT object with proper header including typ: "JWT"
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            
            // Apply the HMAC signature
            signedJWT.sign(signer);
            
            // Serialize to compact form
            return signedJWT.serialize();
            
        } catch (JOSEException e) {
            throw new RuntimeException("Error creating JWT token", e);
        }
    }
}