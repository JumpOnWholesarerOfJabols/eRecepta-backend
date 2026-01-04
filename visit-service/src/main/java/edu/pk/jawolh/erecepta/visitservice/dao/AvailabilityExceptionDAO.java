package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.repository.AvailabilityExceptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
public class AvailabilityExceptionDAO implements AvailabilityExceptionRepository {
    private Connection connection;

    public AvailabilityExceptionDAO(@Value("${spring.datasource.url}") String dbUrl, @Value("${spring.datasource.username}") String username, @Value("${spring.datasource.password}") String password) {
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS AVAILABILITY_EXCEPTION(id varchar(255) PRIMARY KEY, doctorId varchar(255), exceptionDate date, startTime time, endTime time)");
            st.close();
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    @Override
    public boolean save(AvailabilityException avex) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO AVAILABILITY_EXCEPTION VALUES(?, ?, ?, ?, ?)")) {
            statement.setString(1, avex.getId().toString());
            statement.setString(2, avex.getDoctorId().toString());
            statement.setDate(3, Date.valueOf(avex.getExceptionDate()));
            statement.setTime(4, Time.valueOf(avex.getStartTime()));
            statement.setTime(5, Time.valueOf(avex.getEndTime()));

            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<AvailabilityException> findById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE id = ?")) {
            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return Optional.of(mapFromResult(resultSet));

            return Optional.empty();
        } catch (SQLException ex) {
            System.err.println("Error connecting to database");
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<AvailabilityException> findAllByDoctorId(UUID doctorId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ?")) {
            List<AvailabilityException> exceptions = new ArrayList<>();
            statement.setString(1, doctorId.toString());

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                exceptions.add(mapFromResult(result));
            }

            return exceptions;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<AvailabilityException> findAllByDoctorIdAndDateEquals(UUID doctorId, LocalDate date) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ? AND exceptionDate = ?")) {
            List<AvailabilityException> exceptions = new ArrayList<>();

            statement.setString(1, doctorId.toString());
            statement.setDate(2, Date.valueOf(date));

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                exceptions.add(mapFromResult(result));
            }

            return exceptions;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, LocalDate dateStart, LocalDate dateEnd) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ? AND exceptionDate BETWEEN ? AND ?")) {
            List<AvailabilityException> exceptions = new ArrayList<>();

            statement.setString(1, doctorId.toString());
            statement.setDate(2, Date.valueOf(dateStart));
            statement.setDate(3, Date.valueOf(dateEnd));

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                exceptions.add(mapFromResult(result));
            }

            return exceptions;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsByIdAndDoctorIdEquals(UUID id, UUID doctorId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM AVAILABILITY_EXCEPTION WHERE id = ? AND doctorId = ?")) {
            statement.setString(1, id.toString());
            statement.setString(2, doctorId.toString());

            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM AVAILABILITY_EXCEPTION WHERE id = ?")) {
            statement.setString(1, id.toString());
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return false;
        }
    }

    private AvailabilityException mapFromResult(ResultSet rs) throws SQLException {
        AvailabilityException avex = new AvailabilityException(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)));
        avex.setExceptionDate(rs.getDate(3).toLocalDate());
        avex.setStartTime(rs.getTime(4).toLocalTime());
        avex.setEndTime(rs.getTime(5).toLocalTime());

        return avex;
    }
}
