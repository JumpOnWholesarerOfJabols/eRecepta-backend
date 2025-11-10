package edu.pk.jawolh.erecepta.visitservice;

import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.model.Visit;
import edu.pk.jawolh.erecepta.visitservice.repository.VisitRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestingGrounds {
    private final VisitRepository visitRepository;

    TestingGrounds(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;

        visitRepository.findAll().forEach(System.out::println);

        Visit v = new Visit("drmario", "luigi", Specialization.DERMATOLOGY);
        v.setVisitTime(LocalDateTime.now().plusDays(24));

        visitRepository.save(v);

        visitRepository.findById(1).ifPresent(System.out::println);
        visitRepository.findAll().forEach(System.out::println);
    }
}
