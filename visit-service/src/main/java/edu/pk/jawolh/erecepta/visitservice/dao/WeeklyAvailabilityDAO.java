package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.repository.WeeklyAvailabilityReporitory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WeeklyAvailabilityDAO implements WeeklyAvailabilityReporitory {
    private Connection connection;

    private WeeklyAvailabilityDAO(@Value("${spring.datasource.url}") String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl, "admin", "password");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS WEEKLY_AVAILABILITY(doctorId uuid, dayOfWeek tinyint, startTime time, endTime time, CONSTRAINT PK_WA PRIMARY KEY (doctorId, dayOfWeek))");
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    @Override
    public void save(WeeklyAvailability availability) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO WEEKLY_AVAILABILITY VALUES(?, ?, ?, ?)");
            statement.setString(1, availability.getDoctorId().toString());
            statement.setInt(2, availability.getDayOfWeek().getValue());
            statement.setTime(3, Time.valueOf(availability.getStartTime()));
            statement.setTime(4, Time.valueOf(availability.getEndTime()));

            statement.execute();
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
        }
    }

    @Override
    public List<WeeklyAvailability> findAllByDoctorId(String doctorId) {
        try {
            List<WeeklyAvailability> availabilities = new ArrayList<>();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM WEEKLY_AVAILABILITY WHERE doctorId = ?");
            statement.setString(1, doctorId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                availabilities.add(mapFromResult(result));
            }

            return availabilities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(String doctorId, DayOfWeek dayOfWeek) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM WEEKLY_AVAILABILITY WHERE doctorId = ? AND dayOfWeek = ?");
            statement.setString(1, doctorId);
            statement.setInt(2, dayOfWeek.getValue());

            ResultSet result = statement.executeQuery();
            if (result.next())
                return Optional.of(mapFromResult(result));
            else
                return Optional.empty();
        } catch (SQLException ex) {
            System.err.println("Error connecting to database");
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private WeeklyAvailability mapFromResult(ResultSet result) throws SQLException {
        WeeklyAvailability availability = new WeeklyAvailability(UUID.fromString(result.getString(1)), DayOfWeek.of(result.getInt(2)));
        availability.setStartTime(result.getTime(3).toLocalTime());
        availability.setEndTime(result.getTime(4).toLocalTime());

        return availability;
    }
}
