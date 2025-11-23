package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import edu.pk.jawolh.erecepta.visitservice.exception.InvalidTimeConstraintException;
import edu.pk.jawolh.erecepta.visitservice.exception.WeeklyAvailabilityNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.WeeklyAvailabilityInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.repository.WeeklyAvailabilityReporitory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyAvailabilityService {
    private final WeeklyAvailabilityReporitory repository;
    private final WeeklyAvailabilityInputMapper mapper;

    public boolean createWeeklyAvailability(UUID doctorId, CreateWeeklyAvailabilityInput input) {
        log.info("Creating weekly availability for doctor: {} on {}", doctorId, input.getDayOfWeek());
        
        WeeklyAvailability weeklyAvailability = mapper.mapFromInput(doctorId, input);

        if (weeklyAvailability.getStartTime().isAfter(weeklyAvailability.getEndTime())) {
            log.warn("Invalid time range: startTime {} is after endTime {}", 
                weeklyAvailability.getStartTime(), weeklyAvailability.getEndTime());
            throw new InvalidTimeConstraintException("startTime cannot be before endTime");
        }

        boolean result;
        if (repository.existsByDoctorIdAndDayOfWeekEquals(doctorId, weeklyAvailability.getDayOfWeek())) {
            log.info("Updating existing weekly availability for doctor: {} on {}", doctorId, weeklyAvailability.getDayOfWeek());
            result = repository.update(weeklyAvailability);
        } else {
            log.info("Saving new weekly availability for doctor: {} on {}", doctorId, weeklyAvailability.getDayOfWeek());
            result = repository.save(weeklyAvailability);
        }
        
        log.info("Weekly availability operation completed: {}", result);
        return result;
    }

    public List<WeeklyAvailability> findAllByDoctorId(UUID doctorId) {
        log.debug("Finding all weekly availabilities for doctor: {}", doctorId);
        return repository.findAllByDoctorId(doctorId);
    }

    public Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(UUID doctorId, DayOfWeek dayOfWeek) {
        log.debug("Finding weekly availability for doctor: {} on {}", doctorId, dayOfWeek);
        return repository.findByDoctorIdAndDayOfWeekEquals(doctorId, dayOfWeek);
    }

    public boolean deleteWeeklyAvailability(UUID doctorId, DayOfWeek dow) {
        log.info("Deleting weekly availability for doctor: {} on {}", doctorId, dow);
        
        if (!repository.existsByDoctorIdAndDayOfWeekEquals(doctorId, dow)) {
            log.warn("Weekly availability not found for doctor: {} on {}", doctorId, dow);
            throw new WeeklyAvailabilityNotFoundException();
        }

        boolean deleted = repository.deleteByDoctorIdAndDayOfWeekEquals(doctorId, dow);
        log.info("Weekly availability deleted: {}", deleted);
        return deleted;
    }
}
