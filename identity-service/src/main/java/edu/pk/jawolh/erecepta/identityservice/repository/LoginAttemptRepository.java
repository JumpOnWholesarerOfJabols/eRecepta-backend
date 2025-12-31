package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.LoginAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LoginAttemptRepository {

    private final JdbcTemplate jdbcTemplate;

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
}