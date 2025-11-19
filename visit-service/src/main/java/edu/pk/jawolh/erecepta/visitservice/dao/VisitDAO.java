package edu.pk.jawolh.erecepta.visitservice.dao;

import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.model.VisitStatus;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class VisitDAO implements VisitRepository {
    private Connection connection;

    private VisitDAO(@Value("${spring.datasource.url}") String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl, "admin", "password");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS VISIT(id uuid primary key, doctorId uuid, patientId uuid, specialization int, visitTime timestamp, visitStatus int)");
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    public void save(Visit visit) {
        try {
            Statement statement = connection.createStatement();
            ResultSet maxId = statement.executeQuery("select max(id) from visit");

            maxId.next();

            PreparedStatement insert = connection.prepareStatement("insert into visit values(?, ?, ?, ?, ?, ?)");
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
    public Optional<Visit> findById(String id) {
        try {
            PreparedStatement query = connection.prepareStatement("select * from visit where id = ?");
            query.setString(1, id);
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
        try {
            List<Visit> visits = new ArrayList<>();
            PreparedStatement query = connection.prepareStatement("select * from visit");
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

    private Visit mapFromResult(ResultSet result) throws SQLException {
        Visit v = new Visit(UUID.fromString(result.getString(1)), UUID.fromString(result.getString(2)), UUID.fromString(result.getString(3)), Specialization.values()[result.getInt(4)]);
        v.setVisitTime(result.getTimestamp(5).toLocalDateTime());
        v.setVisitStatus(VisitStatus.values()[result.getInt(6)]);

        return v;
    }
}
