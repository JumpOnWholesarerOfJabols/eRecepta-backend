package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.Visit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository {
    void save(Visit visit);

    Optional<Visit> findById(UUID id);
    List<Visit> findAll();

    List<Visit> findAllByVisitTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Visit> findAllByDoctorId(UUID doctorId);

    List<Visit> findAllByPatientId(UUID patientId);

    void deleteById(UUID id);
}
