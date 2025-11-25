package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.visitservice.exception.AvailabilityExceptionCollisionException;
import edu.pk.jawolh.erecepta.visitservice.exception.AvailabilityExceptionNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.exception.InThePastException;
import edu.pk.jawolh.erecepta.visitservice.exception.StartBeforeEndException;
import edu.pk.jawolh.erecepta.visitservice.mapper.AvailabilityExceptionInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.repository.AvailabilityExceptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityExceptionService {
    private final AvailabilityExceptionRepository repository;
    private final AvailabilityExceptionInputMapper mapper;

    public UUID createAvailabilityException(UUID doctorId, CreateAvailabilityExceptionInput input) {
        AvailabilityException avex = mapper.mapFromInput(doctorId, input);

        if (avex.getExceptionDate().isBefore(LocalDate.now())) {
            throw new InThePastException("exceptionDate");
        }

        if (avex.getStartTime().isAfter(avex.getEndTime())) {
            throw new StartBeforeEndException();
        }

        List<AvailabilityException> avexList = repository.findAllByDoctorIdAndDateEquals(doctorId, avex.getExceptionDate());

        for (AvailabilityException collision : avexList) {
            if (avex.getStartTime().isBefore(collision.getEndTime()) &&
                    collision.getStartTime().isBefore(avex.getEndTime())) {
                throw new AvailabilityExceptionCollisionException();
            }
        }

        repository.save(avex);
        return avex.getId();
    }

    public List<AvailabilityException> findAllByDoctorId(UUID doctorId) {
        return repository.findAllByDoctorId(doctorId);
    }

    public List<AvailabilityException> findAllBydDoctorIdAndDateEquals(UUID doctorId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        return repository.findAllByDoctorIdAndDateEquals(doctorId, localDate);
    }

    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, String dateStart, String dateEnd) {
        LocalDate startDate = LocalDate.parse(dateStart);
        LocalDate endDate = LocalDate.parse(dateEnd);
        return repository.findAllByDoctorIdAndDateBetween(doctorId, startDate, endDate);
    }

    public boolean deleteById(UUID doctorId, UUID id) {
        if (!repository.existsByIdAndDoctorIdEquals(id, doctorId))
            throw new AvailabilityExceptionNotFoundException(id);
        return repository.deleteById(id);
    }
}
