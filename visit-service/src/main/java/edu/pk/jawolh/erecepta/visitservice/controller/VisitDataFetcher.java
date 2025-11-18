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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@DgsComponent
@RequiredArgsConstructor
public class VisitDataFetcher {
    private final VisitService service;

    @DgsQuery
    public Optional<Visit> findById(@InputArgument Integer id) {
        return service.findById(id);
    }

    @DgsQuery
    public List<Visit> findAll() {
        return service.findAll();
    }

    @DgsMutation
    @PreAuthorize("hasRole('PATIENT')")
    public int createVisit(@InputArgument CreateVisitInput in) {
        return service.createVisit(getCurrentUserId(), in);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}