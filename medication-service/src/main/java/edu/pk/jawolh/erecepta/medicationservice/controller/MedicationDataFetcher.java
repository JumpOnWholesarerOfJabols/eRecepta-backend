package edu.pk.jawolh.erecepta.medicationservice.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
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
    public List<Medication> medications(MedicationFilterInput filter, Integer limit, Integer offset) {

        log.debug("filter: {}",filter.toString());
        log.debug("limit: {}",limit.toString());
        log.debug("offset: {}",offset.toString());

        return drugService.getMedicationByFilter(filter, limit, offset);
    }

    @DgsQuery
    public Medication medication(UUID id){
        log.debug("id: {}",id.toString());

        return drugService.getMedicationById(id);
    }

    @DgsQuery
    public List<DrugInteraction> checkInteractions(UUID targetMedicationId, List<UUID> currentMedicationIds){
        log.debug("Current medication ids: {}",currentMedicationIds.toString());
        log.debug("Target medication id: {}",targetMedicationId.toString());

        return drugService.checkInteractions(targetMedicationId, currentMedicationIds);
    }

    @DgsMutation
    public Medication createMedication(CreateMedicationInput input){
        log.debug("CreateMedicationInput: {}",input.toString());

        return drugService.createMedication(input);
    }

    @DgsMutation
    public Medication patchMedication(UUID id, PatchMedicationInput input){
        log.debug("PatchMedicationInput: {}",input.toString());

        return drugService.patchMedication(id, input);
    }


}
