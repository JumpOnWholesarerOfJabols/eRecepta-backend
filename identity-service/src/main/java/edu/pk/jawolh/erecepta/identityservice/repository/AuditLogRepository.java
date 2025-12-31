package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.identityservice.model.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AuditLog> auditLogRowMapper = (rs, rowNum) -> AuditLog.builder()
            .id(UUID.fromString(rs.getString("ID")))
            .userId(UUID.fromString(rs.getString("USER_ID")))
            .ipAddress(rs.getString("IP_ADDRESS"))
            .actionName(rs.getString("ACTION_NAME"))
            .logDate(rs.getTimestamp("LOG_DATE").toLocalDateTime())
            .build();

    public void save(AuditLog log) {
        String sql = """
            INSERT INTO AUDIT_LOG (ID, USER_ID, IP_ADDRESS, ACTION_NAME, LOG_DATE) 
            VALUES (?, ?, ?, ?, ?)
        """;

        if (log.getId() == null) {
            log.setId(UUID.randomUUID());
        }

        jdbcTemplate.update(sql,
                log.getId().toString(),
                log.getUserId().toString(),
                log.getIpAddress(),
                log.getActionName(),
                Timestamp.valueOf(log.getLogDate())
        );
    }

    public List<AuditLog> findAllByUserId(UUID userId) {
        String sql = "SELECT * FROM AUDIT_LOG WHERE USER_ID = ? ORDER BY LOG_DATE DESC";
        return jdbcTemplate.query(sql, auditLogRowMapper, userId.toString());
    }
}
