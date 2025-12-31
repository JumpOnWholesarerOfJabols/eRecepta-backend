package edu.pk.jawolh.erecepta.identityservice.service;

import edu.pk.jawolh.erecepta.identityservice.config.JwtProperties;
import edu.pk.jawolh.erecepta.identityservice.model.RefreshToken;
import edu.pk.jawolh.erecepta.identityservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plus(jwtProperties.getRefreshExpiration()))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(token.getToken());
            throw new RuntimeException("Refresh token expired. Please make a new signin request");
        }
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteByUserIdAndTokenNot(UUID userId, String token) {
        refreshTokenRepository.deleteByUserIdAndTokenNot(userId, token);
    }

    public void deleteAllByUserId(UUID userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}