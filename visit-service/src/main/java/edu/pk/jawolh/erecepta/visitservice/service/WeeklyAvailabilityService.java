package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import edu.pk.jawolh.erecepta.visitservice.exception.EndBeforeStartException;
import edu.pk.jawolh.erecepta.visitservice.exception.WeeklyAvailabilityNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.WeeklyAvailabilityInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.repository.WeeklyAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WeeklyAvailabilityService {
    private final WeeklyAvailabilityRepository repository;
    private final WeeklyAvailabilityInputMapper mapper;

    public boolean createWeeklyAvailability(UUID doctorId, CreateWeeklyAvailabilityInput input) {
        WeeklyAvailability weeklyAvailability = mapper.mapFromInput(doctorId, input);

        if (weeklyAvailability.getStartTime().isAfter(weeklyAvailability.getEndTime())) {
            throw new EndBeforeStartException();
        }

        if (repository.existsByDoctorIdAndDayOfWeekEquals(doctorId, weeklyAvailability.getDayOfWeek()))
            return repository.update(weeklyAvailability);

        return repository.save(weeklyAvailability);
    }

    public List<WeeklyAvailability> findAllByDoctorId(UUID doctorId) {
        return repository.findAllByDoctorId(doctorId);
    }

    public Optional<WeeklyAvailability> findByDoctorIdAndDayOfWeekEquals(UUID doctorId, DayOfWeek dayOfWeek) {
        return repository.findByDoctorIdAndDayOfWeekEquals(doctorId, dayOfWeek);
    }

    public boolean deleteWeeklyAvailability(UUID doctorId, DayOfWeek dow) {
        if (!repository.existsByDoctorIdAndDayOfWeekEquals(doctorId, dow))
            throw new WeeklyAvailabilityNotFoundException(doctorId, dow);

        return repository.deleteByDoctorIdAndDayOfWeekEquals(doctorId, dow);
    }
}
