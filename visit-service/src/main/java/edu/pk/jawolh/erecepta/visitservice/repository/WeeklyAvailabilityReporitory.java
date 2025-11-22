package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeeklyAvailabilityReporitory {
    void save(WeeklyAvailability availability);

    List<WeeklyAvailability> findAllByDoctorId(UUID doctorId);

    Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(UUID doctorId, DayOfWeek dayOfWeek);

    void deleteByDoctorIdAndDayOfWeekEquals(UUID doctorId, DayOfWeek dayOfWeek);
}
