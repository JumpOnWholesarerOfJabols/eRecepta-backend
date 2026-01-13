package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class VisitDAO implements VisitRepository {
    private Connection connection;

    public VisitDAO(@Value("${spring.datasource.url}") String dbUrl, @Value("${spring.datasource.username}") String username, @Value("${spring.datasource.password}") String password) {
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS VISIT(id varchar(255) primary key, doctorId varchar(255), patientId varchar(255), specialization int, visitTime timestamp, visitStatus int)");
            st.close();
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    @Override
    public void save(Visit visit) {
        try (PreparedStatement insert = connection.prepareStatement("insert into visit values(?, ?, ?, ?, ?, ?)")) {
            insert.setString(1, visit.getId().toString());
            insert.setString(2, visit.getDoctorId().toString());
            insert.setString(3, visit.getPatientId().toString());
            insert.setInt(4, visit.getSpecialization().ordinal());
            insert.setTimestamp(5, Timestamp.valueOf(visit.getVisitTime()));
            insert.setInt(6, visit.getVisitStatus().ordinal());

            insert.execute();
        } catch (SQLException ex) {
            System.err.println("Error trying to insert visit");
            ex.printStackTrace();
        }
    }

    @Override
    public Optional<Visit> findById(UUID id) {
        try (PreparedStatement query = connection.prepareStatement("select * from visit where id = ?")) {
            query.setString(1, id.toString());
            ResultSet result = query.executeQuery();

            if (!result.next())
                return Optional.empty();

            return Optional.of(mapFromResult(result));
        } catch (SQLException ex) {
            System.err.println("Error trying to find visit: " + id);
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Visit> findAll() {
        try (PreparedStatement query = connection.prepareStatement("select * from visit")) {
            List<Visit> visits = new ArrayList<>();
            ResultSet result = query.executeQuery();

            while (result.next()) {
                visits.add(mapFromResult(result));
            }
            return visits;
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Visit> findAllByDoctorIdAndVisitTimeBetween(UUID doctorId, LocalDateTime start, LocalDateTime end) {
        try (PreparedStatement query = connection.prepareStatement("select * from visit where doctorId = ? AND visitTime between ? and ?")) {
            List<Visit> visits = new ArrayList<>();

            query.setString(1, doctorId.toString());
            query.setTimestamp(2, Timestamp.valueOf(start));
            query.setTimestamp(3, Timestamp.valueOf(end));
            ResultSet result = query.executeQuery();

            while (result.next()) {
                visits.add(mapFromResult(result));
            }
            return visits;
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Visit> findAllByDoctorId(UUID doctorId) {
        try (PreparedStatement query = connection.prepareStatement("select * from visit where doctorId = ?")) {
            List<Visit> visits = new ArrayList<>();

            query.setString(1, doctorId.toString());
            ResultSet result = query.executeQuery();

            while (result.next()) {
                visits.add(mapFromResult(result));
            }
            return visits;
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Visit> findAllByPatientId(UUID patientId) {
        try (PreparedStatement query = connection.prepareStatement("select * from visit where patientId = ?")) {
            List<Visit> visits = new ArrayList<>();

            query.setString(1, patientId.toString());
            ResultSet result = query.executeQuery();

            while (result.next()) {
                visits.add(mapFromResult(result));
            }
            return visits;
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        try (PreparedStatement query = connection.prepareStatement("select 1 from visit where id = ?")) {
            query.setString(1, id.toString());
            ResultSet result = query.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByIdAndDoctorIdEqualsOrPatientIdEquals(UUID id, UUID doctorId, UUID patientId) {
        try (PreparedStatement query = connection.prepareStatement("select 1 from visit where id = ? and (doctorId = ? or patientId = ?)")) {
            query.setString(1, id.toString());
            query.setString(2, doctorId.toString());
            query.setString(3, patientId.toString());

            ResultSet result = query.executeQuery();
            return result.next();
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM visit WHERE id = ?")) {
            statement.setString(1, id.toString());
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateVisitTime(UUID id, LocalDateTime newVisitTime) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE VISIT SET visitTime = ? WHERE id = ?")) {
            statement.setTimestamp(1, Timestamp.valueOf(newVisitTime));
            statement.setString(2, id.toString());
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateVisitStatus(UUID id, VisitStatus newVisitStatus) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE VISIT SET visitStatus = ? WHERE id = ?")) {
            statement.setInt(1, newVisitStatus.ordinal());
            statement.setString(2, id.toString());
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    private Visit mapFromResult(ResultSet result) throws SQLException {
        Visit v = new Visit(UUID.fromString(result.getString(1)), UUID.fromString(result.getString(2)), UUID.fromString(result.getString(3)), Specialization.values()[result.getInt(4)]);
        v.setVisitTime(result.getTimestamp(5).toLocalDateTime());
        v.setVisitStatus(VisitStatus.values()[result.getInt(6)]);

        return v;
    }

    @Override
    public boolean cancelAllByDoctorIdOrPatientId(UUID userId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE VISIT SET visitStatus = ? WHERE doctorId = ? OR patientId = ?")) {
            statement.setInt(1, VisitStatus.CANCELLED.ordinal());
            statement.setString(2, userId.toString());
            statement.setString(3, userId.toString());
            statement.execute();
            return true;
        } catch (SQLException e) {
            log.warn("Error cancelling visits for user: {}", userId);
            return false;
        }
    }
}
