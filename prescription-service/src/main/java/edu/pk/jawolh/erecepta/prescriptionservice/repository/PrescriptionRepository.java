package edu.pk.jawolh.erecepta.prescriptionservice.repository;

import edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findAllByPatientId(UUID patientId);

    Optional<Prescription> findByVisitIdAndDoctorIdOrPatientId(UUID visitId, UUID doctorId, UUID patientId);

    boolean existsByVisitId(UUID visitId);
}
