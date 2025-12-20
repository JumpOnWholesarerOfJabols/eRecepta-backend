package edu.pk.jawolh.erecepta.prescriptionservice.controller;

import com.example.demo.codegen.types.CreatePrescriptionInput;
import com.example.demo.codegen.types.Prescription;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PrescribedMedicationRequest;
import edu.pk.jawolh.erecepta.prescriptionservice.dto.PrescriptionRequest;
import edu.pk.jawolh.erecepta.prescriptionservice.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class PrescriptionDataFetcher extends AbstractDataFetcher {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public Prescription createPrescription(@InputArgument CreatePrescriptionInput input) {
        log.debug("Creating prescription for visit {} by doctor {}", input.getVisitId(), getCurrentUserId());

        // Convert GraphQL input to service request
        List<PrescribedMedicationRequest> medicationRequests = input.getMedications().stream()
                .map(medInput -> new PrescribedMedicationRequest(
                        UUID.fromString(medInput.getMedicationId()),
                        medInput.getUsageNotes()
                ))
                .collect(Collectors.toList());

        PrescriptionRequest request = new PrescriptionRequest(
                UUID.fromString(input.getVisitId()),
                medicationRequests
        );

        edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription prescription =
                prescriptionService.createPrescription(request, getCurrentUserId());

        return prescriptionMapper.toGraphQLType(prescription);
    }

    @DgsQuery
    public Prescription prescription(@InputArgument String id) {
        log.debug("Fetching prescription {}", id);
        edu.pk.jawolh.erecepta.prescriptionservice.model.Prescription prescription =
                prescriptionService.getPrescription(UUID.fromString(id));
        return prescriptionMapper.toGraphQLType(prescription);
    }

    @DgsQuery
    public List<Prescription> prescriptions() {
        log.debug("Fetching all prescriptions");
        return prescriptionService.getAllPrescriptions().stream()
                .map(prescriptionMapper::toGraphQLType)
                .collect(Collectors.toList());
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).ADMINISTRATOR.name())")
    public Boolean deletePrescription(@InputArgument String id) {
        log.debug("Deleting prescription {} by user {}", id, getCurrentUserId());
        prescriptionService.deletePrescription(UUID.fromString(id));
        return true;
    }
}
