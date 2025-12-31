package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.LoginAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LoginAttemptRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<LoginAttempt> loginAttemptRowMapper = (rs, rowNum) -> LoginAttempt.builder()
            .id(UUID.fromString(rs.getString("ID")))
            .userId(UUID.fromString(rs.getString("USER_ID")))
            .ipAddress(rs.getString("IP_ADDRESS"))
            .success(rs.getBoolean("SUCCESS"))
            .attemptDate(rs.getTimestamp("ATTEMPT_DATE").toLocalDateTime())
            .build();

    public void save(LoginAttempt attempt) {
        String sql = """
            INSERT INTO LOGIN_ATTEMPTS (ID, USER_ID, IP_ADDRESS, SUCCESS, ATTEMPT_DATE)
            VALUES (?, ?, ?, ?, ?)
        """;

        if (attempt.getId() == null) {
            attempt.setId(UUID.randomUUID());
        }

        jdbcTemplate.update(sql,
                attempt.getId().toString(),
                attempt.getUserId().toString(),
                attempt.getIpAddress(),
                attempt.isSuccess() ? 1 : 0,
                Timestamp.valueOf(attempt.getAttemptDate())
        );
    }

    public List<LoginAttempt> findAllByUserId(UUID userId) {
        String sql = "SELECT * FROM LOGIN_ATTEMPTS WHERE USER_ID = ? ORDER BY ATTEMPT_DATE DESC";
        return jdbcTemplate.query(sql, loginAttemptRowMapper, userId.toString());
    }
}