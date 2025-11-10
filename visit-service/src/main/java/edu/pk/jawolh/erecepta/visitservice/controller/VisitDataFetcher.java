package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateVisitInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.mapper.VisitInputMapper;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;

import java.util.List;
import java.util.Optional;

@DgsComponent
public record VisitDataFetcher(VisitRepository visitRepository, VisitInputMapper mapper) {
    @DgsQuery
    public Optional<Visit> findById(@InputArgument Integer id) {
        return visitRepository.findById(id);
    }

    @DgsQuery
    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    @DgsMutation
    public int createVisit(@InputArgument CreateVisitInput in) {
        return visitRepository.save(mapper.mapFromInput(in));
    }
}
