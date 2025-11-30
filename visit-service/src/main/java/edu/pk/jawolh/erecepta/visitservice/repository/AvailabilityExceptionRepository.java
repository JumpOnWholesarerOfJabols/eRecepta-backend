package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvailabilityExceptionRepository {
    boolean save(AvailabilityException avex);

    Optional<AvailabilityException> findById(UUID id);

    List<AvailabilityException> findAllByDoctorId(UUID doctorId);

    List<AvailabilityException> findAllByDoctorIdAndDateEquals(UUID doctorId, LocalDate date);

    List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, LocalDate dateStart, LocalDate dateEnd);

    boolean existsByIdAndDoctorIdEquals(UUID id, UUID doctorId);

    boolean deleteById(UUID id);
}
