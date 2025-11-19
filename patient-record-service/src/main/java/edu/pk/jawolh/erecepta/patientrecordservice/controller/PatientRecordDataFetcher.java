package edu.pk.jawolh.erecepta.patientrecordservice.controller;

import com.example.demo.codegen.types.PatientInfo;
import com.netflix.graphql.dgs.DgsComponent;
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

}
