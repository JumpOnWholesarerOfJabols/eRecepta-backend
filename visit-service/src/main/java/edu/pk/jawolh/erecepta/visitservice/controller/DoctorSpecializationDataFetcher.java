package edu.pk.jawolh.erecepta.visitservice.controller;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.Specialization;
import edu.pk.jawolh.erecepta.visitservice.service.DoctorSpecializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class DoctorSpecializationDataFetcher extends AbstractDataFetcher {
    private final DoctorSpecializationService service;

    @DgsQuery
    public List<Specialization> findAllSpecializations(@InputArgument UUID doctorId) {
        log.info("GraphQL Query: findAllSpecializations for doctor: {}", doctorId);
        return service.getSpecializations(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public boolean createSpecialization(@InputArgument Specialization specialization) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: createSpecialization {} by user: {}", specialization, userId);
        return service.createDoctorSpecialization(userId, specialization);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public boolean deleteSpecialization(@InputArgument Specialization specialization) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: deleteSpecialization {} by user: {}", specialization, userId);
        return service.deleteDoctorSpecialization(userId, specialization);
    }

}
