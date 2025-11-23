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

    public boolean createDoctorSpecialization(UUID doctorId, Specialization specialization) {
        if (repository.existsByDoctorIdAndSpecializationEquals(doctorId, specialization))
            throw new IllegalArgumentException("DoctorSpecialization already exists");

        return repository.save(new DoctorSpecialization(doctorId, specialization));
    }

    public List<Specialization> getSpecializations(UUID doctorId) {
        return repository.findAllByDoctorId(doctorId).stream().map(DoctorSpecialization::specialization).toList();
    }

    public boolean deleteDoctorSpecialization(UUID doctorId, Specialization specialization) {
        if (!repository.existsByDoctorIdAndSpecializationEquals(doctorId, specialization))
            throw new IllegalArgumentException("DoctorSpecialization not found");

        return repository.deleteByDoctorIdAndSpecializationEquals(doctorId, specialization);
    }
}
