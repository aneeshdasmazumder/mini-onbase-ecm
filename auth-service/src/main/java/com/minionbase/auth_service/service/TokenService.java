package com.minionbase.auth_service.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final Key signingKey;
    private final String issuer;
    private final int expiresMinutes;

    public TokenService(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.issuer}") String issuer,
                    @Value("${jwt.expires-minutes}") int expiresMinutes) {
        try {
            byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                try { keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secret); } catch (Exception ignored) {}
            }
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret too short. Provide >=32 bytes or a base64 string.");
            }
            this.signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
            this.issuer = issuer;
            this.expiresMinutes = expiresMinutes;
        } catch (Exception ex) {
            // Print concise, helpful message and rethrow to stop startup with clear cause
            System.err.println("TokenService init failed: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw new IllegalStateException("Failed to initialize TokenService: " + ex.getMessage(), ex);
        }
    }

    public String generateToken(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + expiresMinutes * 60L * 1000L);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .claim("roles", roles)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    public List<String> rolesFromClaims(Claims claims) {
        Object raw = claims.get("roles");
        if (raw == null) return List.of();
        if (raw instanceof List) {
            return ((List<?>) raw).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of(raw.toString());
    }
}

