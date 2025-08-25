package com.vesco.financetracker.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private final long expirationMs;

    private final SecretKey key;

    // Constructor injection ensures values are available and key is initialized.
    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.expirationMs = expirationMs;
        // Keys.hmacShaKeyFor requires a sufficiently long key (>= 256 bits for HS256).
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Expose expiration for cookie Max-Age calculation
    public long getExpirationMs() {
        return this.expirationMs;
    }
}
