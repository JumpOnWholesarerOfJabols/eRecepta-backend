package edu.pk.jawolh.erecepta.visitservice.repository;

import edu.pk.jawolh.erecepta.visitservice.model.Visit;

import java.util.List;
import java.util.Optional;

public interface VisitRepository {
    void save(Visit visit);

    Optional<Visit> findById(String id);

    List<Visit> findAll();
}
