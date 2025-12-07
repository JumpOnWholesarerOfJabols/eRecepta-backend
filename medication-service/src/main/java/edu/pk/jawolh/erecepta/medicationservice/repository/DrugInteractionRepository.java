package edu.pk.jawolh.erecepta.medicationservice.repository;

import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteraction;
import edu.pk.jawolh.erecepta.medicationservice.model.DrugInteractionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, DrugInteractionId> {

    @Query("SELECT di FROM DrugInteraction di WHERE di.id.medicationA.id = :medicationId OR di.id.medicationB.id = :medicationId")
    Iterable<DrugInteraction> findInteractionsForMedicationId(@Param("medicationId") UUID medicationId);
}