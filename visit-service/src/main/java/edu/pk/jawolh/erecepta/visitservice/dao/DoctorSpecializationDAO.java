package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.repository.DoctorSpecializationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class DoctorSpecializationDAO implements DoctorSpecializationRepository {
    private Connection connection;

    public DoctorSpecializationDAO(@Value("${spring.datasource.url}") String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl, "admin", "password");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS DOCTOR_SPECIALIZATION(doctorId uuid, specialization tinyint, CONSTRAINT PK_DS PRIMARY KEY (doctorId, specialization))");
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    @Override
    public void save(DoctorSpecialization doctorSpecialization) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO DOCTOR_SPECIALIZATION VALUES(?, ?)")) {
            statement.setString(1, doctorSpecialization.doctorId().toString());
            statement.setInt(2, doctorSpecialization.specialization().ordinal());
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(DoctorSpecialization doctorSpecialization) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM DOCTOR_SPECIALIZATION WHERE doctorId = ? AND specialization = ?")) {
            statement.setString(1, doctorSpecialization.doctorId().toString());
            statement.setInt(2, doctorSpecialization.specialization().ordinal());
            statement.execute();
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
        }
    }

    @Override
    public List<DoctorSpecialization> findAllByDoctorId(UUID doctorId) {
        List<DoctorSpecialization> doctorSpecializations = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM DOCTOR_SPECIALIZATION WHERE doctorId = ?")) {
            statement.setString(1, doctorId.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DoctorSpecialization ds = new DoctorSpecialization(UUID.fromString(resultSet.getString(1)), Specialization.values()[resultSet.getInt(2)]);
                doctorSpecializations.add(ds);
            }

            return doctorSpecializations;
        } catch (SQLException e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
