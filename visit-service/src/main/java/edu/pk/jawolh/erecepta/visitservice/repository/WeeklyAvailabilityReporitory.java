package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface WeeklyAvailabilityReporitory {
    void save(WeeklyAvailability availability);

    List<WeeklyAvailability> findAllByDoctorId(String doctorId);

    Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(String doctorId, DayOfWeek dayOfWeek);
}
