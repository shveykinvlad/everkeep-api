package com.everkeep.service.security;

import static java.nio.charset.StandardCharsets.UTF_16;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.everkeep.config.properties.JwtProperties;
import com.everkeep.model.User;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final Clock clock;

    public Claims getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes(UTF_16))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String generateToken(User user) {
        var claims = Map.of("roles", user.getRolesNames());
        var expirationSeconds = Long.parseLong(jwtProperties.getExpirationTimeSec());
        var creationDate = Date.from(OffsetDateTime.now(clock).toInstant());
        var expirationDate = Date.from(OffsetDateTime.now(clock).plusSeconds(expirationSeconds).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(UTF_16)))
                .compact();
    }
}