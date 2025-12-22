package edu.pk.jawolh.erecepta.med_docs_service.repository;

import edu.pk.jawolh.erecepta.med_docs_service.model.Prescription;
import edu.pk.jawolh.erecepta.med_docs_service.model.PrescriptionStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class PrescriptionDAO {

    private final EntityManager entityManager;

    public List<Prescription> findPrescriptions(UUID patientId,
                                                PrescriptionStatus status,
                                                Integer limit,
                                                Integer offset) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Prescription> criteriaQuery = criteriaBuilder.createQuery(Prescription.class);
        Root<Prescription> root = criteriaQuery.from(Prescription.class);

        List<Predicate> predicates = new ArrayList<>();

        if (patientId != null) {
            predicates.add(criteriaBuilder.equal(root.get("patientId"), patientId));
        }

        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("created")));

        TypedQuery<Prescription> query = entityManager.createQuery(criteriaQuery);

        if (offset != null && offset > 0) {
            query.setFirstResult(offset);
        }

        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }
}