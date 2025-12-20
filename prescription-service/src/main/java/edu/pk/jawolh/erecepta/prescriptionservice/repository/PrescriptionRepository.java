package edu.pk.jawolh.erecepta.prescriptionservice.repository;

import edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
