package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;

import java.util.List;
import java.util.UUID;

public interface DoctorSpecializationRepository {
    void save(DoctorSpecialization doctorSpecialization);

    void delete(DoctorSpecialization doctorSpecialization);

    List<DoctorSpecialization> findAllByDoctorId(UUID doctorId);
}
