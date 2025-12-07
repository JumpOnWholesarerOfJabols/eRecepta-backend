package edu.pk.jawolh.erecepta.medicationservice.repository;

import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteraction;
import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteractionId;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, DrugInteractionId> {}
