package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.exception.DoctorNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.model.VisitStatus;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
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
    private final VisitRepository visitRepository;
    private final VisitInputMapper mapper;
    private final GrpcDoctorService grpcDoctorService;

    private final WeeklyAvailabilityService weeklyAvailabilityService;
    private final AvailabilityExceptionService availabilityExceptionService;

    public UUID createVisit(String patientId, CreateVisitInput input) {
        if (!grpcDoctorService.checkDoctorExists(input.getDoctorId())) {
            throw new DoctorNotFoundException(input.getDoctorId());
        }

        LocalDateTime vdt = LocalDateTime.parse(input.getVisitTime());

        if (vdt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("visitTime cannot be set in the past");
        }

        Optional<WeeklyAvailability> av = weeklyAvailabilityService.findByDoctorIdAndDayOfWeekEquals(input.getDoctorId(), vdt.getDayOfWeek());

        if (av.isEmpty()) {
            throw new IllegalArgumentException("Doctor does not accept visits on this day of week");
        }
        if (vdt.toLocalTime().isBefore(av.get().getStartTime()) || vdt.toLocalTime().isAfter(av.get().getEndTime())) {
            throw new IllegalArgumentException("Cannot create a visit outside of the doctor's availability window");
        }

        List<AvailabilityException> exceptionList = availabilityExceptionService.findAllBydDoctorIdAndDateEquals(input.getDoctorId(), vdt.toLocalDate().toString());

        for (AvailabilityException avex : exceptionList) {
            if (vdt.toLocalTime().isAfter(avex.getStartTime()) && vdt.toLocalTime().isBefore(avex.getEndTime())) {
                throw new IllegalArgumentException("Cannot create a visit outside of the doctor's availability window");
            }
        }

        Visit v = mapper.mapFromInput(patientId, input);
        v.setVisitStatus(VisitStatus.SCHEDULED);
        visitRepository.save(v);

        return v.getId();
    }

    public Optional<Visit> findById(String id) {
        return visitRepository.findById(id);
    }

    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

}
