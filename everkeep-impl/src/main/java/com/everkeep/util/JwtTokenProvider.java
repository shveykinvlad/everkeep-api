package com.everkeep.util;

import static java.nio.charset.StandardCharsets.UTF_16;

import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.everkeep.model.security.User;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expirationTime;

    public String extractUsername(String authToken) {
        return getClaimsFromToken(authToken)
                .getSubject();
    }

    public Claims getClaimsFromToken(String authToken) {
        var key = Base64.getEncoder().encodeToString(secret.getBytes(UTF_16));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRolesNames());

        var expirationSeconds = Long.parseLong(expirationTime);
        var creationDate = Date.from(OffsetDateTime.now().toInstant());
        var expirationDate = Date.from(OffsetDateTime.now().plusSeconds(expirationSeconds).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(UTF_16)))
                .compact();
    }
}