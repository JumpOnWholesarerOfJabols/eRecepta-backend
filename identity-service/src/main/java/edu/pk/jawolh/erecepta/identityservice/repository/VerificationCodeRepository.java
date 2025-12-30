package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.UserVerificationCode;
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
public class VerificationCodeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserVerificationCode> rowMapper = (rs, rowNum) -> UserVerificationCode.builder()
            .id(UUID.fromString(rs.getString("ID")))
            .userId(UUID.fromString(rs.getString("USER_ID")))
            .code(rs.getString("CODE"))
            .expiryDate(rs.getTimestamp("EXPIRY_DATE").toLocalDateTime())
            .build();

    public void save(UserVerificationCode code) {
        if (code.getId() == null) code.setId(UUID.randomUUID());

        String sql = """
            INSERT INTO VERIFICATION_CODE (ID, USER_ID, CODE, EXPIRY_DATE)
            VALUES (?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                code.getId().toString(),
                code.getUserId().toString(),
                code.getCode(),
                Timestamp.valueOf(code.getExpiryDate())
        );
    }

    public Optional<UserVerificationCode> findByUserId(UUID userId) {
        String sql = "SELECT * FROM VERIFICATION_CODE WHERE USER_ID = ?";
        try {
            UserVerificationCode code = jdbcTemplate.queryForObject(sql, rowMapper, userId.toString());
            return Optional.ofNullable(code);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUserId(UUID userId) {
        String sql = "SELECT COUNT(*) FROM VERIFICATION_CODE WHERE USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId.toString());
        return count != null && count > 0;
    }

    public void deleteAllByUserId(UUID userId) {
        String sql = "DELETE FROM VERIFICATION_CODE WHERE USER_ID = ?";
        jdbcTemplate.update(sql, userId.toString());
    }

    public void delete(UserVerificationCode code) {
        String sql = "DELETE FROM VERIFICATION_CODE WHERE ID = ?";
        jdbcTemplate.update(sql, code.getId().toString());
    }
}