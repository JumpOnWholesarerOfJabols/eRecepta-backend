package edu.pk.jawolh.erecepta.medicationservice.repository;

import com.example.demo.codegen.types.MedicationFilterInput;
import edu.pk.jawolh.erecepta.medicationservice.model.Medication;
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

@RequiredArgsConstructor
@Repository
public class MedicationDAO {
    private final EntityManager entityManager;

    public List<Medication> findByFilter(MedicationFilterInput filter, Integer limit, Integer offset) {

        if (filter == null) {
            filter = MedicationFilterInput.newBuilder().build();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Medication> criteriaQuery = criteriaBuilder.createQuery(Medication.class);
        Root<Medication> root = criteriaQuery.from(Medication.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getRequiresPrescription() != null) {
            predicates.add(criteriaBuilder.equal(root.get("requiresPrescription"), filter.getRequiresPrescription()));
        }

        if (filter.getManufacturer() != null) {
            predicates.add(criteriaBuilder.equal(root.get("manufacturer"), filter.getManufacturer()));
        }

        if (filter.getAtcCode() != null) {
            predicates.add(criteriaBuilder.equal(root.get("atcCode"), filter.getAtcCode()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            Predicate tradeNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("tradeName")), searchPattern);
            Predicate genericNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("genericName")), searchPattern);
            predicates.add(criteriaBuilder.or(tradeNameLike, genericNameLike));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Medication> query = entityManager.createQuery(criteriaQuery);

        if (offset != null && offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

}