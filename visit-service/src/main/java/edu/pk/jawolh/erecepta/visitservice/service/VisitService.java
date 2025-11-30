package edu.pk.jawolh.erecepta.visitservice.service;

import com.example.demo.codegen.types.CreateVisitInput;
import edu.pk.jawolh.erecepta.visitservice.exception.VisitNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.model.VisitStatus;
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

    public UUID createVisit(UUID patientId, CreateVisitInput input) {
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

    public boolean deleteById(UUID id) {
        if (!visitRepository.existsById(id))
            throw new VisitNotFoundException(id);

        return visitRepository.deleteById(id);
    }

    public boolean updateVisitTime(UUID id, UUID userId, LocalDateTime vdt) {
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId))
            throw new VisitNotFoundException(id);
        return visitRepository.updateVisitTime(id, vdt);
    }

    public void cancelVisitsInBulk(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        findAllByDoctorIdAndVisitTimeBetween(doctorId, startTime, endTime)
                .forEach(v -> visitRepository.updateVisitStatus(v.getId(), VisitStatus.CANCELLED));
    }

    public boolean updateVisitStatus(UUID id, UUID userId, VisitStatus status) {
        if (!visitRepository.existsByIdAndDoctorIdEqualsOrPatientIdEquals(id, userId, userId))
            throw new VisitNotFoundException(id);

        return visitRepository.updateVisitStatus(id, status);
    }

    public List<Visit> findAllByDoctorIdAndVisitTimeBetween(UUID doctorId, LocalDateTime start, LocalDateTime end) {
        return visitRepository.findAllByDoctorIdAndVisitTimeBetween(doctorId, start, end);
    }
}
