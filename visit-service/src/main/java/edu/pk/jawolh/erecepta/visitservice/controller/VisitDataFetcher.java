package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateVisitInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class VisitDataFetcher extends AbstractDataFetcher {
    private final VisitService service;

    @DgsQuery
    public Optional<Visit> findVisitById(@InputArgument UUID id) {
        log.info("GraphQL Query: findVisitById with id: {}", id);
        return service.findById(id);
    }

    @DgsQuery
    public List<Visit> findAllVisits() {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Query: findAllVisits by user: {}", userId);
        if (hasRole("ROLE_ADMIN"))
            return service.findAll();
        else if (hasRole("ROLE_DOCTOR"))
            return service.findAllByDoctorId(userId);
        else
            return service.findAllByPatientId(userId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('PATIENT')")
    public UUID createVisit(@InputArgument CreateVisitInput visitInput) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: createVisit by user: {}", userId);
        return service.createVisit(userId, visitInput);
    }

    @DgsMutation
    public boolean updateVisitTime(@InputArgument UUID visitId, @InputArgument String newVisitDateTime) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: updateVisitTime for visit: {} by user: {}", visitId, userId);
        return service.updateVisitTime(visitId, userId, newVisitDateTime);
    }

    @DgsMutation
    public boolean deleteVisit(@InputArgument UUID visitId) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: deleteVisit for visit: {} by user: {}", visitId, userId);
        return service.deleteById(visitId, userId);
    }
}