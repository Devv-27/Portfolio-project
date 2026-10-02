package com.debabrata.portfolio;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** Creates and reads JWT tokens. */
@Component
public class Jwt {
    private final SecretKey key;
    private final long minutes;

    public Jwt(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiry-minutes}") long minutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.minutes = minutes;
    }

    public String create(String username) {
        Date now = new Date();
        return Jwts.builder().subject(username).issuedAt(now)
                .expiration(new Date(now.getTime() + minutes * 60_000)).signWith(key).compact();
    }

    /** Returns the username, or null if the token is invalid/expired. */
    public String read(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
