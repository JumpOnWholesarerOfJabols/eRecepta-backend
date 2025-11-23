package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.exception.DoctorNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.exception.InvalidTimeConstraintException;
import edu.pk.jawolh.erecepta.visitservice.exception.VisitNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.*;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        log.info("Creating visit for patient: {} with doctor: {}", patientId, input.getDoctorId());
        
        if (!grpcDoctorService.checkDoctorExists(input.getDoctorId())) {
            log.warn("Doctor not found: {}", input.getDoctorId());
            throw new DoctorNotFoundException(input.getDoctorId());
        }

        LocalDateTime vdt = LocalDateTime.parse(input.getVisitTime());
        UUID doctorId = UUID.fromString(input.getDoctorId());
        Specialization sp = Specialization.valueOf(input.getSpecialization().name());

        if (!doctorSpecializationService.getSpecializations(doctorId).contains(sp)) {
            log.warn("Doctor {} does not accept visits for specialization: {}", doctorId, sp);
            throw new InvalidTimeConstraintException("Doctor does not accept visits for this specialization");
        }

        checkTimeConstraints(doctorId, vdt);

        Visit v = mapper.mapFromInput(patientId, input);
        v.setVisitStatus(VisitStatus.SCHEDULED);
        visitRepository.save(v);

        log.info("Visit created successfully with id: {}", v.getId());
        return v.getId();
    }

    public Optional<Visit> findById(UUID id) {
        log.debug("Finding visit by id: {}", id);
        return visitRepository.findById(id);
    }

    public List<Visit> findAll() {
        log.debug("Finding all visits");
        return visitRepository.findAll();
    }

    public List<Visit> findAllByDoctorId(UUID doctorId) {
        log.debug("Finding all visits for doctor: {}", doctorId);
        return visitRepository.findAllByDoctorId(doctorId);
    }

    public List<Visit> findAllByPatientId(UUID patientId) {
        log.debug("Finding all visits for patient: {}", patientId);
        return visitRepository.findAllByPatientId(patientId);
    }

    public boolean deleteById(UUID id, UUID userId) {
        log.info("Deleting visit: {} by user: {}", id, userId);
        
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId)) {
            log.warn("Visit not found or user not authorized: visitId={}, userId={}", id, userId);
            throw new VisitNotFoundException(id.toString());
        }

        boolean deleted = visitRepository.deleteById(id);
        log.info("Visit deleted: {}, success: {}", id, deleted);
        return deleted;
    }

    public boolean updateVisitTime(UUID id, UUID userId, String newVisitTime) {
        log.info("Updating visit time for visit: {} by user: {} to: {}", id, userId, newVisitTime);
        
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId)) {
            log.warn("Visit not found or user not authorized: visitId={}, userId={}", id, userId);
            throw new VisitNotFoundException(id.toString());
        }

        UUID doctorId = findById(id).orElseThrow().getDoctorId();

        LocalDateTime vdt = LocalDateTime.parse(newVisitTime);
        checkTimeConstraints(doctorId, vdt);

        boolean updated = visitRepository.updateVisitTime(id, vdt);
        log.info("Visit time updated: {}, success: {}", id, updated);
        return updated;
    }

    private void checkTimeConstraints(UUID doctorId, LocalDateTime vdt) {
        log.debug("Checking time constraints for doctor: {} at time: {}", doctorId, vdt);
        
        if (vdt.isBefore(LocalDateTime.now())) {
            log.warn("Attempted to schedule visit in the past: {}", vdt);
            throw new InvalidTimeConstraintException("visitTime cannot be set in the past");
        }

        Optional<WeeklyAvailability> av = weeklyAvailabilityService.findByDoctorIdAndDayOfWeekEquals(doctorId, vdt.getDayOfWeek());

        if (av.isEmpty()) {
            log.warn("Doctor {} does not accept visits on {}", doctorId, vdt.getDayOfWeek());
            throw new InvalidTimeConstraintException("Doctor does not accept visits on this day of week");
        }
        if (vdt.toLocalTime().isBefore(av.get().getStartTime()) || vdt.toLocalTime().isAfter(av.get().getEndTime())) {
            log.warn("Visit time {} outside availability window for doctor {}: {} - {}", 
                vdt.toLocalTime(), doctorId, av.get().getStartTime(), av.get().getEndTime());
            throw new InvalidTimeConstraintException("Cannot create a visit outside of the doctor's availability window");
        }

        List<AvailabilityException> exceptionList = availabilityExceptionService.findAllBydDoctorIdAndDateEquals(doctorId, vdt.toLocalDate().toString());

        for (AvailabilityException avex : exceptionList) {
            if (vdt.toLocalTime().isAfter(avex.getStartTime()) && vdt.toLocalTime().isBefore(avex.getEndTime())) {
                log.warn("Visit time {} conflicts with availability exception for doctor {}", vdt.toLocalTime(), doctorId);
                throw new InvalidTimeConstraintException("Cannot create a visit outside of the doctor's availability window");
            }
        }

        if (!visitRepository.findAllByVisitTimeBetween(vdt, vdt.plusMinutes(VISIT_DURATION_MINUTES)).isEmpty()) {
            log.warn("Visit time {} collides with another visit for doctor {}", vdt, doctorId);
            throw new InvalidTimeConstraintException("Cannot create a visit colliding with another visit");
        }
        
        log.debug("Time constraints check passed for doctor: {} at time: {}", doctorId, vdt);
    }
}
