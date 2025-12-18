package edu.pk.jawolh.erecepta.med_docs_service.controller;

import com.example.demo.codegen.types.IssuePrescriptionInput;
import com.example.demo.codegen.types.Prescription;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.med_docs_service.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class PrescriptionDataFetcher {

    private final PrescriptionService prescriptionService;

    @DgsQuery
    public Prescription verifyPrescription(
            @InputArgument String accessCode,
            @InputArgument UUID patientIdentifier){

        return prescriptionService.verifyPrescription(accessCode, patientIdentifier);
    }

    //issuePrescription(input: IssuePrescriptionInput!): Prescription
    @DgsMutation
    public Prescription issuePrescription(IssuePrescriptionInput input){

        return prescriptionService.issuePrescription(input);
    }
}
