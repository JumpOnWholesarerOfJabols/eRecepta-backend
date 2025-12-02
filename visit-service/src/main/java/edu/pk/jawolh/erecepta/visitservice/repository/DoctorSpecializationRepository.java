package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;

import java.util.List;
import java.util.UUID;

public interface DoctorSpecializationRepository {
    boolean save(DoctorSpecialization doctorSpecialization);

    boolean existsByDoctorIdAndSpecializationEquals(UUID doctorId, Specialization specialization);

    boolean deleteByDoctorIdAndSpecializationEquals(UUID doctorId, Specialization specialization);

    List<DoctorSpecialization> findAll();
    List<DoctorSpecialization> findAllByDoctorId(UUID doctorId);

    List<DoctorSpecialization> findAllBySpecializationEquals(Specialization specialization);
}
