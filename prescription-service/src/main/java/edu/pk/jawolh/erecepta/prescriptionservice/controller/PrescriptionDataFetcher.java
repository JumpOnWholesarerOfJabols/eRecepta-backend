package edu.pk.jawolh.erecepta.prescriptionservice.controller;

import com.example.demo.codegen.types.CreatePrescriptionInput;
import com.example.demo.codegen.types.Prescription;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.prescriptionservice.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class PrescriptionDataFetcher {
    private final PrescriptionService service;


    @DgsQuery
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).PATIENT.name())")
    public List<Prescription> getPrescriptions() {
        return service.getPrescriptions(getCurrentUserId());
    }

    @DgsQuery
    public Optional<Prescription> getPrescriptionByVisitId(@InputArgument UUID visitId) {
        return service.getPrescriptionByVisitId(visitId, getCurrentUserId());
    }

    @DgsQuery
    public Optional<Prescription> getPrescriptionByCode(@InputArgument String code) {
        return service.getPrescriptionByCode(code);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public Prescription createPrescription(@InputArgument CreatePrescriptionInput input) {
        return service.createPrescription(getCurrentUserId(), input);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getName());
    }
}
