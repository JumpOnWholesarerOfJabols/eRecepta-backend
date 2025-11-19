package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.repository.AvailabilityExceptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class AvailabilityExceptionDAO implements AvailabilityExceptionRepository {
    private Connection connection;

    private AvailabilityExceptionDAO(@Value("${spring.datasource.url}") String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl, "admin", "password");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS AVAILABILITY_EXCEPTION(id uuid PRIMARY KEY, doctorId uuid, exceptionDate date, startTime time, endTime time)");
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    @Override
    public void save(AvailabilityException avex) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO AVAILABILITY_EXCEPTION VALUES(?, ?, ?, ?, ?)");
            statement.setString(1, avex.getId().toString());
            statement.setString(2, avex.getDoctorId().toString());
            statement.setDate(3, Date.valueOf(avex.getExceptionDate()));
            statement.setTime(4, Time.valueOf(avex.getStartTime()));
            statement.setTime(5, Time.valueOf(avex.getEndTime()));

            statement.execute();
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
        }
    }

    @Override
    public List<AvailabilityException> findAllByDoctorId(String doctorId) {
        try {
            List<AvailabilityException> exceptions = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ?");
            statement.setString(1, doctorId);

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
    public List<AvailabilityException> findAllByDoctorIdAndDateEquals(String doctorId, LocalDate date) {
        try {
            List<AvailabilityException> exceptions = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ? AND exceptionDate = ?");
            statement.setString(1, doctorId);
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
    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(String doctorId, LocalDate dateStart, LocalDate dateEnd) {
        try {
            List<AvailabilityException> exceptions = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM AVAILABILITY_EXCEPTION WHERE doctorId = ? AND exceptionDate BETWEEN ? AND ?");
            statement.setString(1, doctorId);
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

    private AvailabilityException mapFromResult(ResultSet rs) throws SQLException {
        AvailabilityException avex = new AvailabilityException(UUID.fromString(rs.getString(1)), UUID.fromString(rs.getString(2)));
        avex.setExceptionDate(rs.getDate(3).toLocalDate());
        avex.setStartTime(rs.getTime(4).toLocalTime());
        avex.setEndTime(rs.getTime(5).toLocalTime());

        return avex;
    }
}
