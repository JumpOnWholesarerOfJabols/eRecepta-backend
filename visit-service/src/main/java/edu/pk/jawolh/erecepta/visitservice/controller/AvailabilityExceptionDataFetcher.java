package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.facade.AvailabilityExceptionFacade;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class AvailabilityExceptionDataFetcher extends AbstractDataFetcher {
    private final AvailabilityExceptionFacade facade;

    @DgsQuery
    public List<AvailabilityException> findAllAvailabilityExceptions(@InputArgument UUID doctorId, @InputArgument Optional<String> startDate, @InputArgument Optional<String> endDate) {
        if (startDate.isPresent() && endDate.isPresent()) {
            return facade.findAllByDoctorIdAndDateBetween(doctorId, startDate.get(), endDate.get());
        } else if (startDate.isPresent() || endDate.isPresent()) {
            throw new IllegalArgumentException("startDate and endDate must both be provided or both be omitted");
        }

        return facade.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).ADMINISTRATOR.name())")
    public UUID createAvailabilityException(@InputArgument CreateAvailabilityExceptionInput avexInput) {
        return facade.createAvailabilityException(getCurrentUserId(), avexInput);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean deleteAvailabilityException(@InputArgument UUID avexId) {
        return facade.deleteById(getCurrentUserId(), avexId);
    }
}
