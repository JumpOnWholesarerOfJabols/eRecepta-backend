package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class VisitDAO implements VisitRepository {
    private Connection connection;

    private VisitDAO(@Value("${spring.datasource.url}") String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl, "admin", "password");
            connection.createStatement().execute("CREATE TABLE VISIT(id int primary key, doctorId varchar(255), patientId varchar(255), specialization int, visitTime timestamp)");
        } catch (SQLException ex) {
            System.err.println("Error connecting to database: " + dbUrl);
            ex.printStackTrace();
        }
    }

    public int save(Visit visit) {
        try {
            Statement statement = connection.createStatement();
            ResultSet maxId = statement.executeQuery("select max(id) from visit");

            maxId.next();
            int id = maxId.getInt(1) + 1;

            visit.setId(id);

            PreparedStatement insert = connection.prepareStatement("insert into visit values(?, ?, ?, ?, ?)");
            insert.setInt(1, id);
            insert.setString(2, visit.getDoctorId());
            insert.setString(3, visit.getPatientId());
            insert.setInt(4, visit.getSpecialization().ordinal());
            insert.setTimestamp(5, Timestamp.valueOf(visit.getVisitTime()));

            insert.execute();

            return id;
        } catch (SQLException ex) {
            System.err.println("Error trying to insert visit");
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public Optional<Visit> findById(int id) {
        try {
            PreparedStatement query = connection.prepareStatement("select * from visit where id = ?");
            query.setInt(1, id);
            ResultSet result = query.executeQuery();

            if (!result.next())
                return Optional.empty();

            Visit v = new Visit(result.getString(2), result.getString(3), Specialization.values()[result.getInt(4)]);
            v.setId(result.getInt(1));
            v.setVisitTime(result.getTimestamp(5).toLocalDateTime());

            return Optional.of(v);
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
                Visit v = new Visit(result.getString(2), result.getString(3), Specialization.values()[result.getInt(4)]);
                v.setId(result.getInt(1));
                v.setVisitTime(result.getTimestamp(5).toLocalDateTime());

                visits.add(v);
            }

            return visits;
        } catch (SQLException ex) {
            System.err.println("Error trying to find all visits");
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
