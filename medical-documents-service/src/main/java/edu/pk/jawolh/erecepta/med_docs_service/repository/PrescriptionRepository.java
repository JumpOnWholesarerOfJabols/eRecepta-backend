package edu.pk.jawolh.erecepta.med_docs_service.repository;

import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByPatientIdAndAccessCode(UUID patientId, String accessCode);
}
