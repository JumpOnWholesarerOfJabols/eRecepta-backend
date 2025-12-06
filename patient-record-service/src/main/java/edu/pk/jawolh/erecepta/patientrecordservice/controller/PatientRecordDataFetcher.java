package edu.pk.jawolh.erecepta.patientrecordservice.controller;

import com.example.demo.codegen.types.PatientInfo;
import com.example.demo.codegen.types.UpdatePatientInfoInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.patientrecordservice.service.PatientRecordService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class PatientRecordDataFetcher {

    private final PatientRecordService patientRecordService;

    @DgsQuery
    public PatientInfo getPatientRecordByUserId(UUID userId) {
        return patientRecordService.getPatientInfo(userId);
    }


    @DgsMutation
    public PatientInfo updatePatientInfo(
            @InputArgument UUID userId,
            @InputArgument UpdatePatientInfoInput input) {

        return patientRecordService.updatePatientInfo(userId, input);
    }

    @DgsMutation
    public PatientInfo addAllergy(
            @InputArgument UUID userId,
            @InputArgument String allergy) {

        return patientRecordService.addAllergy(userId, allergy);
    }

    @DgsMutation
    public PatientInfo removeAllergy(
            @InputArgument UUID userId,
            @InputArgument String allergy) {

        return patientRecordService.removeAllergy(userId, allergy);
    }


    @DgsMutation
    public PatientInfo addMedication(
            @InputArgument UUID userId,
            @InputArgument String medication) {

        return patientRecordService.addMedication(userId, medication);
    }

    @DgsMutation
    public PatientInfo removeMedication(
            @InputArgument UUID userId,
            @InputArgument String medication) {

        return patientRecordService.removeMedication(userId, medication);
    }


    @DgsMutation
    public PatientInfo addChronicDisease(
            @InputArgument UUID userId,
            @InputArgument String disease) {

        return patientRecordService.addChronicDisease(userId, disease);
    }

    @DgsMutation
    public PatientInfo removeChronicDisease(
            @InputArgument UUID userId,
            @InputArgument String disease) {

        return patientRecordService.addChronicDisease(userId, disease);
    }
}
