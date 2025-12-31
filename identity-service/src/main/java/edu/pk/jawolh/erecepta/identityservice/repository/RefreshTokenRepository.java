package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<RefreshToken> rowMapper = (rs, rowNum) -> RefreshToken.builder()
            .id(UUID.fromString(rs.getString("ID")))
            .userId(UUID.fromString(rs.getString("USER_ID")))
            .token(rs.getString("TOKEN"))
            .expiryDate(rs.getTimestamp("EXPIRY_DATE").toLocalDateTime())
            .build();

    public void save(RefreshToken token) {
        String sql = "INSERT INTO REFRESH_TOKEN (ID, USER_ID, TOKEN, EXPIRY_DATE) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                token.getId().toString(),
                token.getUserId().toString(),
                token.getToken(),
                Timestamp.valueOf(token.getExpiryDate()));
    }

    public Optional<RefreshToken> findByToken(String token) {
        String sql = "SELECT * FROM REFRESH_TOKEN WHERE TOKEN = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, token));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteByToken(String token) {
        String sql = "DELETE FROM REFRESH_TOKEN WHERE TOKEN = ?";
        jdbcTemplate.update(sql, token);
    }

    public void deleteByUserIdAndTokenNot(UUID userId, String tokenToKeep) {
        String sql = "DELETE FROM REFRESH_TOKEN WHERE USER_ID = ? AND TOKEN != ?";
        jdbcTemplate.update(sql, userId.toString(), tokenToKeep);
    }

    public void deleteAllByUserId(UUID userId) {
        String sql = "DELETE FROM REFRESH_TOKEN WHERE USER_ID = ?";
        jdbcTemplate.update(sql, userId.toString());
    }
}