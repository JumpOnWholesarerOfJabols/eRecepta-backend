package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import edu.pk.jawolh.erecepta.visitservice.mapper.WeeklyAvailabilityInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.repository.WeeklyAvailabilityReporitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeeklyAvailabilityService {
    private final WeeklyAvailabilityReporitory repository;
    private final WeeklyAvailabilityInputMapper mapper;

    public DayOfWeek createWeeklyAvailability(UUID doctorId, CreateWeeklyAvailabilityInput input) {
        WeeklyAvailability weeklyAvailability = mapper.mapFromInput(doctorId, input);

        if (weeklyAvailability.getStartTime().isAfter(weeklyAvailability.getEndTime())) {
            throw new IllegalArgumentException("startTime cannot be before endTime");
        }

        repository.save(weeklyAvailability);
        return weeklyAvailability.getDayOfWeek();
    }

    public List<WeeklyAvailability> findAllByDoctorId(UUID doctorId) {
        return repository.findAllByDoctorId(doctorId);
    }

    public Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(UUID doctorId, DayOfWeek dayOfWeek) {
        return repository.findByDoctorIdAndDayOfWeekEquals(doctorId, dayOfWeek);
    }
}
