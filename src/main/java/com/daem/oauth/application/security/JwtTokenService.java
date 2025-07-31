package com.daem.oauth.application.security;

import com.daem.oauth.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for JWT token generation, validation, and parsing.
 */
@Service
public class JwtTokenService {
    
    private final SecretKey secretKey;
    
    @Value("${app.jwt.access-token-expiration:3600}")
    private Long accessTokenExpiration; // default 1 hour
    
    @Value("${app.jwt.refresh-token-expiration:604800}")
    private Long refreshTokenExpiration; // default 7 days
    
    @Value("${app.jwt.issuer:oauth-server}")
    private String issuer;

    public JwtTokenService(@Value("${app.jwt.secret:a-very-long-and-secure-secret-key-for-hs256}") String secret) {
        if (secret.length() < 32) {
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }

    /**
     * Generates a JWT access token from an Authentication object.
     */
    public String generateToken(Authentication authentication) {
        // In a real app, you might get the User object from the principal
        // For now, we'll use the username and authorities from the Authentication object itself.
        String username = authentication.getName();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));
        claims.put("token_type", "access");
        
        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * Generates an access token from a User domain object.
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));
        claims.put("token_type", "access");
        
        return createToken(claims, user.getUsername(), accessTokenExpiration);
    }

    /**
     * Generates a refresh token from a User domain object.
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("token_type", "refresh");
        
        return createToken(claims, user.getUsername(), refreshTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expirationSeconds) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(expirationSeconds, ChronoUnit.SECONDS);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            return getClaimsFromToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
