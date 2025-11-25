package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.exception.*;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.*;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitService {
    private static final int VISIT_DURATION_MINUTES = 20;

    private final VisitRepository visitRepository;
    private final VisitInputMapper mapper;
    private final GrpcDoctorService grpcDoctorService;

    private final WeeklyAvailabilityService weeklyAvailabilityService;
    private final AvailabilityExceptionService availabilityExceptionService;
    private final DoctorSpecializationService doctorSpecializationService;

    public UUID createVisit(UUID patientId, CreateVisitInput input) {
        if (!grpcDoctorService.checkDoctorExists(input.getDoctorId())) {
            throw new DoctorNotFoundException(UUID.fromString(input.getDoctorId()));
        }

        LocalDateTime vdt = LocalDateTime.parse(input.getVisitTime());
        UUID doctorId = UUID.fromString(input.getDoctorId());
        Specialization sp = Specialization.valueOf(input.getSpecialization().name());

        if (!doctorSpecializationService.getSpecializations(doctorId).contains(sp)) {
            throw new DoctorSpecializationNotFoundException(doctorId, sp);
        }

        checkTimeConstraints(doctorId, vdt);

        Visit v = mapper.mapFromInput(patientId, input);
        v.setVisitStatus(VisitStatus.SCHEDULED);
        visitRepository.save(v);

        return v.getId();
    }

    public Optional<Visit> findById(UUID id) {
        return visitRepository.findById(id);
    }

    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    public List<Visit> findAllByDoctorId(UUID doctorId) {
        return visitRepository.findAllByDoctorId(doctorId);
    }

    public List<Visit> findAllByPatientId(UUID patientId) {
        return visitRepository.findAllByPatientId(patientId);
    }

    public boolean deleteById(UUID id, UUID userId) {
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId))
            throw new VisitNotFoundException(id);

        return visitRepository.deleteById(id);
    }

    public boolean updateVisitTime(UUID id, UUID userId, String newVisitTime) {
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId))
            throw new VisitNotFoundException(id);

        UUID doctorId = findById(id).orElseThrow().getDoctorId();

        LocalDateTime vdt = LocalDateTime.parse(newVisitTime);
        checkTimeConstraints(doctorId, vdt);

        return visitRepository.updateVisitTime(id, vdt);
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

        if (!visitRepository.findAllByVisitTimeBetween(vdt, vdt.plusMinutes(VISIT_DURATION_MINUTES)).isEmpty())
            throw new VisitCollisionException();
    }
}
