package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.visitservice.exception.DoctorSpecializationAlreadyExistsException;
import edu.pk.jawolh.erecepta.visitservice.exception.DoctorSpecializationNotFoundException;
import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.repository.DoctorSpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorSpecializationService {
    private final DoctorSpecializationRepository repository;

    public boolean createDoctorSpecialization(UUID doctorId, Specialization specialization) {
        log.info("Creating doctor specialization for doctor: {} with specialization: {}", doctorId, specialization);
        
        if (repository.existsByDoctorIdAndSpecializationEquals(doctorId, specialization)) {
            log.warn("Doctor specialization already exists for doctor: {} with specialization: {}", doctorId, specialization);
            throw new DoctorSpecializationAlreadyExistsException();
        }

        boolean saved = repository.save(new DoctorSpecialization(doctorId, specialization));
        log.info("Doctor specialization created: {}", saved);
        return saved;
    }

    public List<Specialization> getSpecializations(UUID doctorId) {
        log.debug("Getting specializations for doctor: {}", doctorId);
        return repository.findAllByDoctorId(doctorId).stream().map(DoctorSpecialization::specialization).toList();
    }

    public boolean deleteDoctorSpecialization(UUID doctorId, Specialization specialization) {
        log.info("Deleting doctor specialization for doctor: {} with specialization: {}", doctorId, specialization);
        
        if (!repository.existsByDoctorIdAndSpecializationEquals(doctorId, specialization)) {
            log.warn("Doctor specialization not found for doctor: {} with specialization: {}", doctorId, specialization);
            throw new DoctorSpecializationNotFoundException();
        }

        boolean deleted = repository.deleteByDoctorIdAndSpecializationEquals(doctorId, specialization);
        log.info("Doctor specialization deleted: {}", deleted);
        return deleted;
    }
}
