package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.visitservice.exception.AvailabilityExceptionNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.exception.InvalidTimeConstraintException;
import edu.pk.jawolh.erecepta.visitservice.mapper.AvailabilityExceptionInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.repository.AvailabilityExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityExceptionService {
    private final AvailabilityExceptionRepository repository;
    private final AvailabilityExceptionInputMapper mapper;

    public UUID createAvailabilityException(UUID doctorId, CreateAvailabilityExceptionInput input) {
        log.info("Creating availability exception for doctor: {} on date: {}", doctorId, input.getExceptionDate());
        
        AvailabilityException avex = mapper.mapFromInput(doctorId, input);

        if (avex.getExceptionDate().isBefore(LocalDate.now())) {
            log.warn("Attempted to create availability exception in the past: {}", avex.getExceptionDate());
            throw new InvalidTimeConstraintException("exceptionDate cannot be set in the past");
        }

        if (avex.getStartTime().isAfter(avex.getEndTime())) {
            log.warn("Invalid time range: startTime {} is after endTime {}", avex.getStartTime(), avex.getEndTime());
            throw new InvalidTimeConstraintException("startTime cannot be before endTime");
        }

        List<AvailabilityException> avexList = repository.findAllByDoctorIdAndDateEquals(doctorId, avex.getExceptionDate());

        for (AvailabilityException collision : avexList) {
            if (avex.getStartTime().isBefore(collision.getEndTime()) &&
                    collision.getStartTime().isBefore(avex.getEndTime())) {
                log.warn("Availability exception collision detected for doctor: {} on date: {}", doctorId, avex.getExceptionDate());
                throw new InvalidTimeConstraintException("AvailabilityExceptions cannot collide");
            }
        }

        repository.save(avex);
        log.info("Availability exception created with id: {}", avex.getId());
        return avex.getId();
    }

    public List<AvailabilityException> findAllByDoctorId(UUID doctorId) {
        log.debug("Finding all availability exceptions for doctor: {}", doctorId);
        return repository.findAllByDoctorId(doctorId);
    }

    public List<AvailabilityException> findAllBydDoctorIdAndDateEquals(UUID doctorId, String date) {
        log.debug("Finding availability exceptions for doctor: {} on date: {}", doctorId, date);
        LocalDate localDate = LocalDate.parse(date);
        return repository.findAllByDoctorIdAndDateEquals(doctorId, localDate);
    }

    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, String dateStart, String dateEnd) {
        log.debug("Finding availability exceptions for doctor: {} between {} and {}", doctorId, dateStart, dateEnd);
        LocalDate startDate = LocalDate.parse(dateStart);
        LocalDate endDate = LocalDate.parse(dateEnd);
        return repository.findAllByDoctorIdAndDateBetween(doctorId, startDate, endDate);
    }

    public boolean deleteById(UUID doctorId, UUID id) {
        log.info("Deleting availability exception: {} for doctor: {}", id, doctorId);
        
        if (!repository.existsByIdAndDoctorIdEquals(id, doctorId)) {
            log.warn("Availability exception not found: {} for doctor: {}", id, doctorId);
            throw new AvailabilityExceptionNotFoundException();
        }
        
        boolean deleted = repository.deleteById(id);
        log.info("Availability exception deleted: {}", deleted);
        return deleted;
    }
}
