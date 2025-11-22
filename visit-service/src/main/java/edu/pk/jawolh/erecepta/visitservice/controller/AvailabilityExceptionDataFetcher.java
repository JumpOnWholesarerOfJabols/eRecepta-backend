package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.service.AvailabilityExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class AvailabilityExceptionDataFetcher extends AbstractDataFetcher {
    private final AvailabilityExceptionService service;

    @DgsQuery
    public List<AvailabilityException> findAllAvailabilityExceptions(@InputArgument UUID doctorId, @InputArgument Optional<String> startDate, @InputArgument Optional<String> endDate) {
        if (startDate.isPresent() && endDate.isPresent()) {
            return service.findAllByDoctorIdAndDateBetween(doctorId, startDate.get(), endDate.get());
        } else if (startDate.isPresent() || endDate.isPresent()) {
            throw new IllegalArgumentException("startDate and endDate must both not be null");
        }

        return service.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public UUID createAvailabilityException(@InputArgument CreateAvailabilityExceptionInput in) {
        return service.createAvailabilityException(getCurrentUserId(), in);
    }
}
