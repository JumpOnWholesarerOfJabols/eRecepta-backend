package edu.pk.jawolh.erecepta.medicationservice.controller;

import com.example.demo.codegen.types.CreateMedicationInput;
import com.example.demo.codegen.types.DrugInteraction;
import com.example.demo.codegen.types.Medication;
import com.example.demo.codegen.types.MedicationFilterInput;
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

        log.info("filter: {}",filter.toString());
        log.info("limit: {}",limit.toString());
        log.info("offset: {}",offset.toString());

        return drugService.getMedicationByFilter(filter, limit, offset);
    }

    @DgsQuery
    public Medication medication(UUID id){
        log.info("id: {}",id.toString());

        return drugService.getMedicationById(id);
    }

    @DgsQuery
    public List<DrugInteraction> checkInteractions(UUID targetMedicationId, List<UUID> currentMedicationIds){
        log.info(currentMedicationIds.toString());
        log.info(targetMedicationId.toString());

        return drugService.checkInteractions(targetMedicationId, currentMedicationIds);
    }

    @DgsMutation
    public Medication createMedication(CreateMedicationInput input){
        log.info(input.toString());

        return drugService.createMedication(input);
    }

    @DgsMutation
    public Medication updateMedication(UUID id, CreateMedicationInput input){
        log.info(input.toString());

        return Medication.newBuilder().id(id.toString()).build();
    }

    @DgsMutation
    public boolean archiveMedication(UUID id){
        log.info(id.toString());

        return true;
    }

}
