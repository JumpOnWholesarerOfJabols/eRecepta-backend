package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityExceptionRepository {
    void save(AvailabilityException avex);

    List<AvailabilityException> findAllByDoctorId(String doctorId);

    List<AvailabilityException> findAllByDoctorIdAndDateEquals(String doctorId, LocalDate date);

    List<AvailabilityException> findAllByDoctorIdAndDateBetween(String doctorId, LocalDate dateStart, LocalDate dateEnd);
}
