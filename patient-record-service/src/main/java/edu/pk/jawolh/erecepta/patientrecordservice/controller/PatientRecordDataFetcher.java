package edu.pk.jawolh.erecepta.patientrecordservice.controller;

import com.example.demo.codegen.types.PatientInfo;
import com.example.demo.codegen.types.UpdatePatientInfoInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
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
    public PatientInfo updatePatientInfo(UUID userId, UpdatePatientInfoInput input) {

        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }

    @DgsMutation
    public PatientInfo addAllergy(UUID userId, String allergy) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }

    @DgsMutation
    public PatientInfo removeAllergy(UUID userId, String allergy) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }


    @DgsMutation
    public PatientInfo addMedication(UUID userId, String medication) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }

    @DgsMutation
    public PatientInfo removeMedication(UUID userId, String medication) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }


    @DgsMutation
    public PatientInfo addChronicDisease(UUID userId, String medication) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }

    @DgsMutation
    public PatientInfo removeChronicDisease(UUID userId, String medication) {
        System.out.println("Test");

        return PatientInfo.newBuilder().build();
    }








//    addChronicDisease(userId: ID!, disease: String!): PatientInfo
//    removeChronicDisease(userId: ID!, disease: String!): PatientInfo
}
