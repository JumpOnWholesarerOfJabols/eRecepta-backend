package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.exception.InvalidQueryParametersException;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import edu.pk.jawolh.erecepta.visitservice.service.AvailabilityExceptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class AvailabilityExceptionDataFetcher extends AbstractDataFetcher {
    private final AvailabilityExceptionService service;

    @DgsQuery
    public List<AvailabilityException> findAllAvailabilityExceptions(@InputArgument UUID doctorId, @InputArgument Optional<String> startDate, @InputArgument Optional<String> endDate) {
        log.info("GraphQL Query: findAllAvailabilityExceptions for doctor: {}", doctorId);
        if (startDate.isPresent() && endDate.isPresent()) {
            return service.findAllByDoctorIdAndDateBetween(doctorId, startDate.get(), endDate.get());
        } else if (startDate.isPresent() || endDate.isPresent()) {
            log.warn("Invalid query parameters: both startDate and endDate must be provided or both must be null");
            throw new InvalidQueryParametersException("startDate and endDate must both not be null");
        }

        return service.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public UUID createAvailabilityException(@InputArgument CreateAvailabilityExceptionInput avexInput) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: createAvailabilityException by user: {}", userId);
        return service.createAvailabilityException(userId, avexInput);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public boolean deleteAvailabilityException(@InputArgument UUID avexId) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: deleteAvailabilityException {} by user: {}", avexId, userId);
        return service.deleteById(userId, avexId);
    }
}
