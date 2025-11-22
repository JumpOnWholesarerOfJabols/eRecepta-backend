package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityExceptionRepository {
    void save(AvailabilityException avex);

    List<AvailabilityException> findAllByDoctorId(UUID doctorId);

    List<AvailabilityException> findAllByDoctorIdAndDateEquals(UUID doctorId, LocalDate date);

    List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, LocalDate dateStart, LocalDate dateEnd);

    void deleteById(UUID id);
}
