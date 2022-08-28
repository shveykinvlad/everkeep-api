package com.everkeep.security;

import static java.nio.charset.StandardCharsets.UTF_16;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;

import com.everkeep.config.properties.JwtProperties;
import com.everkeep.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final String ROLES_CLAIM = "roles";

    private final JwtProperties jwtProperties;
    private final Clock clock;

    public Claims getClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.secret().getBytes(UTF_16))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String generateToken(User user) {
        var claims = Map.of(ROLES_CLAIM, user.getRolesNames());
        var creationDate = Date.from(OffsetDateTime.now(clock).toInstant());
        var expirationDate = Date.from(OffsetDateTime.now(clock)
                .plusSeconds(jwtProperties.expiryDuration().getSeconds()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(UTF_16)))
                .compact();
    }
}
