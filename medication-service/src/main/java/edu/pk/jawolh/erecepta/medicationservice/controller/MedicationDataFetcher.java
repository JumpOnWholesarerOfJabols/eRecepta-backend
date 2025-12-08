package edu.pk.jawolh.erecepta.medicationservice.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.medicationservice.service.DrugService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class MedicationDataFetcher {

    private final DrugService drugService;

    @DgsQuery
    public List<Medication> medications(
            @InputArgument MedicationFilterInput filter,
            @InputArgument Integer limit,
            @InputArgument Integer offset) {

        log.debug("Fetching medications with filter: {}, limit: {}, offset: {}", filter, limit, offset);
        return drugService.getMedicationByFilter(filter, limit, offset);
    }

    @DgsQuery
    public Medication medication(@InputArgument UUID id){

        log.debug("Fetching medication with id: {}", id);
        return drugService.getMedicationById(id);
    }

    @DgsQuery
    public List<DrugInteraction> checkInteractions(
            @InputArgument UUID targetMedicationId,
            @InputArgument List<UUID> currentMedicationIds){

        log.debug("Checking interactions for target: {} against list: {}", targetMedicationId, currentMedicationIds);
        return drugService.checkInteractions(targetMedicationId, currentMedicationIds);
    }

    @DgsMutation
    public Medication createMedication(@InputArgument CreateMedicationInput input) {
        log.debug("Creating medication: {}", input);
        return drugService.createMedication(input);
    }

    @DgsMutation
    public Medication patchMedication(@InputArgument UUID id, @InputArgument PatchMedicationInput input) {
        log.debug("Patching medication id: {} with input: {}", id, input);
        return drugService.patchMedication(id, input);
    }

    @DgsMutation
    public Boolean deleteMedication(@InputArgument UUID id) {
        log.debug("Archiving medication id: {}", id);

        return drugService.deleteMedication(id);
    }

    @DgsMutation
    public Medication addIngredient(@InputArgument UUID medicationId, @InputArgument IngredientInput input) {
        log.debug("Adding ingredient to medication {}: {}", medicationId, input);
        return drugService.addIngredient(medicationId, input);
    }

    @DgsMutation
    public Medication updateIngredient(@InputArgument UUID medicationId, @InputArgument UUID ingredientId, @InputArgument UpdateIngredientInput input) {
        log.debug("Updating ingredient {} in medication {}: {}", ingredientId, medicationId, input);
        return drugService.updateIngredient(medicationId, ingredientId, input);
    }
//
//    @DgsMutation
//    public Medication removeIngredient(@InputArgument UUID medicationId, @InputArgument UUID ingredientId) {
//        log.debug("Removing ingredient {} from medication {}", ingredientId, medicationId);
//        return drugService.removeIngredient(medicationId, ingredientId);
//    }
//
//    // --- MUTATIONS: INDICATIONS ---
//
//    @DgsMutation
//    public Medication addIndication(@InputArgument UUID medicationId, @InputArgument String indication) {
//        log.debug("Adding indication to medication {}: {}", medicationId, indication);
//        return drugService.addIndication(medicationId, indication);
//    }
//
//    @DgsMutation
//    public Medication removeIndication(@InputArgument UUID medicationId, @InputArgument String indication) {
//        log.debug("Removing indication from medication {}: {}", medicationId, indication);
//        return drugService.removeIndication(medicationId, indication);
//    }
//
//    // --- MUTATIONS: SIDE EFFECTS ---
//
//    @DgsMutation
//    public Medication addSideEffect(@InputArgument UUID medicationId, @InputArgument String sideEffect) {
//        log.debug("Adding side effect to medication {}: {}", medicationId, sideEffect);
//        return drugService.addSideEffect(medicationId, sideEffect);
//    }
//
//    @DgsMutation
//    public Medication removeSideEffect(@InputArgument UUID medicationId, @InputArgument String sideEffect) {
//        log.debug("Removing side effect from medication {}: {}", medicationId, sideEffect);
//        return drugService.removeSideEffect(medicationId, sideEffect);
//    }
}
