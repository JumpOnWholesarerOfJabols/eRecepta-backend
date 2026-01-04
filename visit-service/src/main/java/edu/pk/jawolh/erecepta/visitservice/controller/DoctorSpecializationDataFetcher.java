package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.DoctorData;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.visitservice.facade.DoctorSpecializationFacade;
import edu.pk.jawolh.erecepta.visitservice.model.DoctorSpecialization;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class DoctorSpecializationDataFetcher extends AbstractDataFetcher {
    private final DoctorSpecializationFacade facade;

    @DgsQuery
    public List<Specialization> findAllSpecializations(@InputArgument UUID doctorId) {
        return facade.getSpecializations(doctorId);
    }

    @DgsQuery
    public List<DoctorData> findAllDoctors(@InputArgument Specialization specialization) {
        return facade.findAllDoctorsBySpecialization(specialization);
    }

    @DgsQuery
    public List<DoctorSpecialization> findAllDoctorSpecializations() {
        return facade.findAll();
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean createSpecialization(@InputArgument Specialization specialization) {
        return facade.createDoctorSpecialization(getCurrentUserId(), specialization);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean deleteSpecialization(@InputArgument Specialization specialization) {
        return facade.deleteDoctorSpecialization(getCurrentUserId(), specialization);
    }

}
