package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateVisitInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class VisitDataFetcher extends AbstractDataFetcher {
    private final VisitService service;

    @DgsQuery
    public Optional<Visit> findVisitById(@InputArgument String id) {
        return service.findById(id);
    }

    @DgsQuery
    public List<Visit> findAllVisits() {
        return service.findAll();
    }

    @DgsMutation
    @PreAuthorize("hasRole('PATIENT')")
    public UUID createVisit(@InputArgument CreateVisitInput in) {
        return service.createVisit(getCurrentUserId(), in);
    }
}