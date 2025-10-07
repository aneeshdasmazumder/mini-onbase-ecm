package com.minionbase.auth_service.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.minionbase.auth_service.model.PasswordResetToken;
import com.minionbase.auth_service.respository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepo;

    // token lifetime in minutes (dev friendly)
    private final long tokenTtlMinutes = 60;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(PasswordResetTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    /**
     * Create a one-time token for given userId and persist it.
     * Returns the token string (in real prod you'd email it; in dev we log/return it).
     */
    public String createTokenForUser(long userId) {
        // generate 32 bytes secure random and base64-url encode (no padding)
        byte[] random = new byte[32];
        secureRandom.nextBytes(random);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);

        Instant expiresAt = Instant.now().plus(tokenTtlMinutes, ChronoUnit.MINUTES);

        PasswordResetToken prt = new PasswordResetToken(userId, token, expiresAt);
        tokenRepo.save(prt);

        return token;
    }

    /**
     * Validate token; if valid return associated userId. Do NOT consume here (controller may decide).
     * Throw IllegalArgumentException for invalid/expired token (controller maps to 400/404).
     */
    public Long validateTokenAndGetUserId(String token) {
        Optional<PasswordResetToken> maybe = tokenRepo.findByToken(token);
        if (maybe.isEmpty()) throw new IllegalArgumentException("Invalid token");

        PasswordResetToken prt = maybe.get();
        if (prt.getExpiresAt().isBefore(Instant.now())) {
            // token expired -> remove and inform caller
            tokenRepo.delete(prt);
            throw new IllegalArgumentException("Token expired");
        }
        return prt.getUserId();
    }

    /**
     * Consume (delete) the token after successful reset.
     */
    public void consumeToken(String token) {
        tokenRepo.deleteByToken(token);
    }

    /**
     * Optional cleanup: delete expired tokens older than X days (cron or manual).
     */
    public void deleteExpired() {
        Instant now = Instant.now();
        tokenRepo.findAll().stream()
                .filter(t -> t.getExpiresAt().isBefore(now))
                .forEach(t -> tokenRepo.delete(t));
    }
}
