package edu.pk.jawolh.erecepta.med_docs_service.controller;

import com.example.demo.codegen.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.med_docs_service.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @DgsQuery
    public List<Prescription> prescriptions(
            @InputArgument UUID patientId,
            @InputArgument PrescriptionStatus status,
            @InputArgument Integer limit,
            @InputArgument Integer offset
    ){
        UUID userId = getCurrentUserId();

        log.info("Logged in user: " + userId);
        log.info("Patient id: {}", patientId);
        logCurrentUserRoles();

        if (hasRole("ROLE_DOCTOR") || hasRole("ROLE_ADMINISTRATOR") || hasRole("ROLE_PHARMACIST"))
            return prescriptionService.findPrescriptions(patientId, status, limit, offset);

        return prescriptionService.findPrescriptions(userId, status, limit, offset);
    }

    @DgsMutation
    public Prescription issuePrescription(
            @InputArgument IssuePrescriptionInput input){

        UUID userId = getCurrentUserId();

        return prescriptionService.issuePrescription(userId, input);
    }

    @DgsMutation
    public FulfillResult fulfillPrescription(
            @InputArgument FulfillPrescriptionInput input){

        UUID userId = getCurrentUserId();

        return prescriptionService.fulfillPrescription(userId, input);
    }

    @DgsMutation
    public Prescription cancelPrescription(
            @InputArgument UUID prescriptionId,
            @InputArgument String reason
    ){
        UUID userId = getCurrentUserId();

        return prescriptionService.cancelPrescription(userId,prescriptionId, reason);
    }

    protected final UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }

    protected final boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    protected void logCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        authentication.getAuthorities().forEach(auth ->
                log.info("User role: {}", auth.getAuthority())
        );
    }
}
