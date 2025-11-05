package edu.pk.jawolh.erecepta.identityservice.service;


import edu.pk.jawolh.erecepta.identityservice.config.JwtProperties;
import edu.pk.jawolh.erecepta.identityservice.dto.JwtTokenDTO;
import edu.pk.jawolh.erecepta.identityservice.model.UserRole;
import edu.pk.jawolh.erecepta.identityservice.util.TimeFormatter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final TimeFormatter timeFormatter;

    public JwtTokenDTO generateToken(UUID id, UserRole role) {

        Instant now = timeFormatter.now();
        Instant expiration = now.plus(jwtProperties.getExpiration());

        log.info("Time to add {}", jwtProperties.getExpiration());
        log.info("Now {}",now);
        log.info("Expiration {}", expiration);

        String token = Jwts.builder()
                .subject(id.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("role", role.name())
                .signWith(getSignInKey())
                .compact();

        String formattedExpiration = timeFormatter.formatInstant(expiration);
        return new JwtTokenDTO(token, formattedExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
