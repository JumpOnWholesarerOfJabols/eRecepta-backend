package edu.pk.jawolh.erecepta.identityservice.repository;

import edu.pk.jawolh.erecepta.common.user.enums.UserGender;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.identityservice.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserAccount> userRowMapper = (rs, rowNum) -> UserAccount.builder()
            .id(UUID.fromString(rs.getString("ID")))
            .email(rs.getString("EMAIL"))
            .pesel(rs.getString("PESEL"))
            .firstName(rs.getString("FIRST_NAME"))
            .lastName(rs.getString("LAST_NAME"))
            .phoneNumber(rs.getString("PHONE_NUMBER"))
            .dateOfBirth(rs.getDate("DATE_OF_BIRTH").toLocalDate())
            .role(UserRole.valueOf(rs.getString("USER_ROLE")))
            .userGender(UserGender.valueOf(rs.getString("USER_GENDER")))
            .hashedPassword(rs.getString("HASHED_PASSWORD"))
            .verified(rs.getInt("VERIFIED") == 1)
            .build();

    public UserAccount save(UserAccount user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            insert(user);
        } else {
            if (existsById(user.getId())) {
                update(user);
            } else {
                insert(user);
            }
        }
        return user;
    }

    private void insert(UserAccount user) {
        String sql = """
            INSERT INTO USER_ACCOUNT (
                ID, EMAIL, PESEL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, 
                DATE_OF_BIRTH, USER_ROLE, USER_GENDER, HASHED_PASSWORD, VERIFIED
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                user.getId().toString(),
                user.getEmail(),
                user.getPesel(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                Date.valueOf(user.getDateOfBirth()),
                user.getRole().name(),
                user.getUserGender().name(),
                user.getHashedPassword(),
                user.isVerified() ? 1 : 0
        );
    }

    public void update(UserAccount user) {
        String sql = """
            UPDATE USER_ACCOUNT 
            SET FIRST_NAME = ?, LAST_NAME = ?, PHONE_NUMBER = ?, 
                USER_GENDER = ?, VERIFIED = ?, HASHED_PASSWORD = ?
            WHERE ID = ?
        """;

        jdbcTemplate.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getUserGender().name(), // Aktualizacja płci
                user.isVerified() ? 1 : 0,
                user.getHashedPassword(),
                user.getId().toString()
        );
    }

    public Optional<UserAccount> findByPeselOrEmail(String pesel, String email) {
        String sql = "SELECT * FROM USER_ACCOUNT WHERE PESEL = ? OR EMAIL = ?";
        try {
            UserAccount user = jdbcTemplate.queryForObject(sql, userRowMapper, pesel, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<UserAccount> findById(UUID id) {
        String sql = "SELECT * FROM USER_ACCOUNT WHERE ID = ?";
        try {
            UserAccount user = jdbcTemplate.queryForObject(sql, userRowMapper, id.toString());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByPeselOrEmail(String pesel, String email) {
        String sql = "SELECT COUNT(*) FROM USER_ACCOUNT WHERE PESEL = ? OR EMAIL = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, pesel, email);
        return count != null && count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM USER_ACCOUNT";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM USER_ACCOUNT WHERE ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id.toString());
        return count != null && count > 0;
    }

    public List<UserAccount> findAll() {
        String sql = "SELECT * FROM USER_ACCOUNT";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM USER_ACCOUNT WHERE ID = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM VERIFICATION_CODE");
        jdbcTemplate.update("DELETE FROM RESET_PASSWORD_CODE");
        jdbcTemplate.update("DELETE FROM REFRESH_TOKEN");

        jdbcTemplate.update("DELETE FROM USER_ACCOUNT");
    }

    public Optional<UUID> findIdByPeselOrEmail(String identifier) {
        String sql = "SELECT ID FROM USER_ACCOUNT WHERE PESEL = ? OR EMAIL = ?";
        try {
            String idStr = jdbcTemplate.queryForObject(sql, String.class, identifier, identifier);
            return Optional.ofNullable(idStr).map(UUID::fromString);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}