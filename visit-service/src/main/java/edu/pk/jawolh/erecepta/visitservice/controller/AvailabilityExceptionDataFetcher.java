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
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class AvailabilityExceptionDataFetcher extends AbstractDataFetcher {
    private final AvailabilityExceptionService service;

    @DgsQuery
    public List<AvailabilityException> findAllAvailabilityExceptions(@InputArgument String doctorId) {
        return service.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public UUID createAvailabilityException(@InputArgument CreateAvailabilityExceptionInput in) {
        return service.createAvailabilityException(getCurrentUserId(), in);
    }
}
