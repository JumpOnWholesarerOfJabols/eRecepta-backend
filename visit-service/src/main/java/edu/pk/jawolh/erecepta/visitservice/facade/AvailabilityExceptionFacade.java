package edu.pk.jawolh.erecepta.visitservice.facade;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.service.AvailabilityExceptionService;
import edu.pk.jawolh.erecepta.visitservice.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AvailabilityExceptionFacade {
    private static final String DATETIME_FORMAT = "%sT%s";

    private final AvailabilityExceptionService service;
    private final VisitService visitService;

    public UUID createAvailabilityException(UUID doctorId, CreateAvailabilityExceptionInput input) {
        UUID avexId = service.createAvailabilityException(doctorId, input);

        LocalDateTime startTime = LocalDateTime.parse(DATETIME_FORMAT.formatted(input.getExceptionDate(), input.getStartTime()));
        LocalDateTime endTime = LocalDateTime.parse(DATETIME_FORMAT.formatted(input.getExceptionDate(), input.getEndTime()));

        visitService.cancelVisitsInBulk(doctorId, startTime, endTime);
        return avexId;
    }

    public List<AvailabilityException> findAllByDoctorId(UUID doctorId) {
        return service.findAllByDoctorId(doctorId);
    }

    public List<AvailabilityException> findAllByDoctorIdAndDateBetween(UUID doctorId, String dateStart, String dateEnd) {
        return service.findAllByDoctorIdAndDateBetween(doctorId, dateStart, dateEnd);
    }

    public boolean deleteById(UUID doctorId, UUID id) {
        return service.deleteById(id, doctorId);
    }
}
