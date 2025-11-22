package edu.pk.jawolh.erecepta.visitservice.service;

import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.repository.DoctorSpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorSpecializationService {
    private final DoctorSpecializationRepository repository;

    public Specialization createDoctorSpecialization(UUID doctorId, Specialization specialization) {
        repository.save(new DoctorSpecialization(doctorId, specialization));
        return specialization;
    }

    public List<Specialization> getSpecializations(UUID doctorId) {
        return repository.findAllByDoctorId(doctorId).stream().map(DoctorSpecialization::specialization).toList();
    }
}
