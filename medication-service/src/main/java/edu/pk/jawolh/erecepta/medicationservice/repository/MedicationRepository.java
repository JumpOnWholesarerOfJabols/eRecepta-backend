package edu.pk.jawolh.erecepta.medicationservice.repository;

import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedicationRepository extends JpaRepository<Medication, UUID> {
    boolean existsByEan(String ean);
}
