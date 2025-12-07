package edu.pk.jawolh.erecepta.visitservice.facade;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.common.visit.dtos.UserDataDTO;
import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.visitservice.client.GrpcUserClient;
import edu.pk.jawolh.erecepta.visitservice.exception.*;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VisitFacade {
    private static final int VISIT_DURATION_MINUTES = 20;

    private final VisitService visitService;
    private final AvailabilityExceptionService availabilityExceptionService;
    private final WeeklyAvailabilityService weeklyAvailabilityService;
    private final DoctorSpecializationService doctorSpecializationService;
    private final GrpcUserClient grpcUserService;
    private final RabbitMQService rabbitMQService;
    private final VisitMapper visitMapper;

    public UUID createVisit(UUID patientId, CreateVisitInput input) {
        if (!grpcUserService.checkDoctorExists(input.getDoctorId())) {
            throw new DoctorNotFoundException(UUID.fromString(input.getDoctorId()));
        }

        LocalDateTime vdt = LocalDateTime.parse(input.getVisitTime());
        UUID doctorId = UUID.fromString(input.getDoctorId());
        Specialization sp = Specialization.valueOf(input.getSpecialization().name());

        if (!doctorSpecializationService.getSpecializations(doctorId).contains(sp)) {
            throw new DoctorSpecializationNotFoundException(doctorId, sp);
        }

        checkTimeConstraints(doctorId, vdt);
        Visit v = visitService.createVisit(patientId, input);

        UserDataDTO patientData = grpcUserService.getUserData(patientId.toString());
        UserDataDTO doctorData = grpcUserService.getUserData(doctorId.toString());
        rabbitMQService.sendVisitChangeEvent(visitMapper.mapToMessage(patientData, doctorData, v));

        return v.getId();
    }

    public Optional<Visit> findById(UUID id) {
        return visitService.findById(id);
    }

    public List<Visit> findAll() {
        return visitService.findAll();
    }

    public List<Visit> findAllByDoctorId(UUID doctorId) {
        return visitService.findAllByDoctorId(doctorId);
    }

    public List<Visit> findAllByPatientId(UUID patientId) {
        return visitService.findAllByPatientId(patientId);
    }

    public boolean deleteById(UUID id) {
        return visitService.deleteById(id);
    }

    public boolean updateVisitTime(UUID id, UUID userId, String newVisitTime) {
        Visit v = visitService.findById(id).orElseThrow(() -> new VisitNotFoundException(id));
        UUID doctorId = v.getDoctorId();

        LocalDateTime vdt = LocalDateTime.parse(newVisitTime);
        checkTimeConstraints(doctorId, vdt);

        boolean success = visitService.updateVisitTime(id, userId, vdt);

        if (success) {
            v.setVisitTime(vdt);

            UserDataDTO patientData = grpcUserService.getUserData(v.getPatientId().toString());
            UserDataDTO doctorData = grpcUserService.getUserData(doctorId.toString());
            rabbitMQService.sendVisitChangeEvent(visitMapper.mapToMessage(patientData, doctorData, v));
        }
        return success;
    }

    public boolean updateVisitStatus(UUID id, UUID userId, VisitStatus status) {
        Visit v = visitService.findById(id).orElseThrow(() -> new VisitNotFoundException(id));
        boolean success = visitService.updateVisitStatus(id, userId, status);

        if (success) {
            v.setVisitStatus(status);

            UserDataDTO patientData = grpcUserService.getUserData(v.getPatientId().toString());
            UserDataDTO doctorData = grpcUserService.getUserData(v.getDoctorId().toString());
            rabbitMQService.sendVisitChangeEvent(visitMapper.mapToMessage(patientData, doctorData, v));
        }

        return success;
    }

    private void checkTimeConstraints(UUID doctorId, LocalDateTime vdt) {
        if (vdt.isBefore(LocalDateTime.now())) {
            throw new InThePastException("visitTime");
        }

        Optional<WeeklyAvailability> av = weeklyAvailabilityService.findByDoctorIdAndDayOfWeekEquals(doctorId, vdt.getDayOfWeek());

        if (av.isEmpty()) {
            throw new WeeklyAvailabilityNotFoundException(doctorId, vdt.getDayOfWeek());
        }
        if (vdt.toLocalTime().isBefore(av.get().getStartTime()) || vdt.toLocalTime().isAfter(av.get().getEndTime())) {
            throw new OutsideAvailabilityException();
        }

        List<AvailabilityException> exceptionList = availabilityExceptionService.findAllByDoctorIdAndDateEquals(doctorId, vdt.toLocalDate().toString());

        for (AvailabilityException avex : exceptionList) {
            if (vdt.toLocalTime().isAfter(avex.getStartTime()) && vdt.toLocalTime().isBefore(avex.getEndTime())) {
                throw new OutsideAvailabilityException();
            }
        }

        if (!visitService.findAllByDoctorIdAndVisitTimeBetween(doctorId, vdt, vdt.plusMinutes(VISIT_DURATION_MINUTES)).isEmpty())
            throw new VisitCollisionException();
    }
}
