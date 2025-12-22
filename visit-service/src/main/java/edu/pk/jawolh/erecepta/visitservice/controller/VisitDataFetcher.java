package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateVisitInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.common.user.enums.UserRole;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import edu.pk.jawolh.erecepta.visitservice.facade.VisitFacade;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class VisitDataFetcher extends AbstractDataFetcher {
    private static final String ROLE_DOCTOR = "ROLE_%s".formatted(UserRole.DOCTOR.name());
    private static final String ROLE_ADMIN = "ROLE_%S".formatted(UserRole.ADMINISTRATOR.name());

    private final VisitFacade facade;

    @DgsQuery
    public Optional<Visit> findVisitById(@InputArgument UUID id) {
        return facade.findById(id);
    }

    @DgsQuery
    public List<Visit> findAllVisits() {
        if (hasRole(ROLE_ADMIN))
            return facade.findAll();
        else if (hasRole(ROLE_DOCTOR))
            return facade.findAllByDoctorId(getCurrentUserId());
        else
            return facade.findAllByPatientId(getCurrentUserId());
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).PATIENT.name())")
    public UUID createVisit(@InputArgument CreateVisitInput visitInput) {
        return facade.createVisit(getCurrentUserId(), visitInput);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name()) or hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).PATIENT.name())")
    public boolean updateVisitTime(@InputArgument UUID visitId, @InputArgument String newVisitDateTime) {
        return facade.updateVisitTime(visitId, getCurrentUserId(), newVisitDateTime);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean updateVisitStatus(@InputArgument UUID visitId, @InputArgument VisitStatus newVisitStatus) {
        return facade.updateVisitStatus(visitId, getCurrentUserId(), newVisitStatus);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).PATIENT.name())")
    public boolean cancelVisit(@InputArgument UUID visitId) {
        return facade.updateVisitStatus(visitId, getCurrentUserId(), VisitStatus.CANCELLED);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).ADMINISTRATOR.name())")
    public boolean deleteVisit(@InputArgument UUID visitId) {
        return facade.deleteById(visitId);
    }
}