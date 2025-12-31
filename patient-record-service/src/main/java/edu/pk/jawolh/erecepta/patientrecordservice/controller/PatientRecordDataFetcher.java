package edu.pk.jawolh.erecepta.patientrecordservice.controller;

import com.example.demo.codegen.types.PatientHistoryEntry;
import com.example.demo.codegen.types.PatientInfo;
import com.example.demo.codegen.types.UpdatePatientInfoInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.patientrecordservice.service.PatientRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class PatientRecordDataFetcher {

    private final PatientRecordService patientRecordService;

    @DgsQuery
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR') or (hasRole('PATIENT') and authentication.name == #userId.toString())")
    public PatientInfo getPatientRecordByUserId(UUID userId) {
        return patientRecordService.getPatientInfo(userId);
    }


    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo updatePatientInfo(
            @InputArgument UUID userId,
            @InputArgument UpdatePatientInfoInput input) {

        return patientRecordService.updatePatientInfo(userId, input);
    }

    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo addAllergy(
            @InputArgument UUID userId,
            @InputArgument String allergy) {

        return patientRecordService.addAllergy(userId, allergy);
    }

    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo removeAllergy(
            @InputArgument UUID userId,
            @InputArgument String allergy) {

        return patientRecordService.removeAllergy(userId, allergy);
    }


    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo addMedication(
            @InputArgument UUID userId,
            @InputArgument UUID medicationId) {

        return patientRecordService.addMedication(userId, medicationId);
    }

    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo addChronicDisease(
            @InputArgument UUID userId,
            @InputArgument String disease) {

        return patientRecordService.addChronicDisease(userId, disease);
    }

    @DgsMutation
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public PatientInfo removeChronicDisease(
            @InputArgument UUID userId,
            @InputArgument String disease) {

        return patientRecordService.removeChronicDisease(userId, disease);
    }

    @DgsQuery
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR') or (hasRole('PATIENT') and authentication.name == #userId.toString())")
    public List<PatientHistoryEntry> getPatientHistory(@InputArgument UUID userId) {
        return patientRecordService.getPatientHistory(userId);
    }
}
