package edu.pk.jawolh.erecepta.med_docs_service.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.med_docs_service.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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

    //todo user id - patient can see only its own prescriptions and doctor can see all
    @DgsQuery
    public List<Prescription> prescriptions(
            @InputArgument UUID patientId,
            @InputArgument PrescriptionStatus status,
            @InputArgument Integer limit,
            @InputArgument Integer offset
    ){

        return prescriptionService.findPrescriptions(patientId, status, limit, offset);
    }


    @DgsMutation
    public Prescription issuePrescription(
            @InputArgument IssuePrescriptionInput input){

        return prescriptionService.issuePrescription(input);
    }

    @DgsMutation
    public FulfillResult fulfillPrescription(
            @InputArgument FulfillPrescriptionInput input){

        return prescriptionService.fulfillPrescription(input);
    }

//    cancelPrescription(prescriptionId: ID!, reason: String!): Prescription
    @DgsMutation
    public Prescription cancelPrescription(
            @InputArgument UUID prescriptionId,
            @InputArgument String reason
    ){

        return prescriptionService.cancelPrescription(prescriptionId, reason);
    }


}
