package edu.pk.jawolh.erecepta.visitservice.facade;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import edu.pk.jawolh.erecepta.visitservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.service.AvailabilityExceptionService;
import edu.pk.jawolh.erecepta.visitservice.service.RabbitMQService;
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
    private final RabbitMQService rabbitMQService;
    private final GrpcUserClient grpcUserService;
    private final VisitMapper visitMapper;

    public UUID createAvailabilityException(UUID doctorId, CreateAvailabilityExceptionInput input) {
        UUID avexId = service.createAvailabilityException(doctorId, input);

        LocalDateTime startTime = LocalDateTime.parse(DATETIME_FORMAT.formatted(input.getExceptionDate(), input.getStartTime()));
        LocalDateTime endTime = LocalDateTime.parse(DATETIME_FORMAT.formatted(input.getExceptionDate(), input.getEndTime()));

        List<Visit> conflicts = visitService.cancelVisitsInBulk(doctorId, startTime, endTime);

        conflicts.forEach(v -> {
            UserDataDTO patientData = grpcUserService.getUserData(v.getPatientId().toString());
            UserDataDTO doctorData = grpcUserService.getUserData(doctorId.toString());
            rabbitMQService.sendVisitChangeEvent(visitMapper.mapToMessage(patientData, doctorData, v));
        });
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
