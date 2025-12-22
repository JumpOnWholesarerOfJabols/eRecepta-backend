package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.service.WeeklyAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class WeeklyAvailabilityDataFetcher extends AbstractDataFetcher {
    private final WeeklyAvailabilityService service;

    @DgsQuery
    public List<WeeklyAvailability> findAllWeeklyAvailabilities(@InputArgument UUID doctorId) {
        return service.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean createWeeklyAvailability(@InputArgument CreateWeeklyAvailabilityInput waInput) {
        return service.createWeeklyAvailability(getCurrentUserId(), waInput);
    }

    @DgsMutation
    @PreAuthorize("hasRole(T(edu.pk.jawolh.erecepta.common.user.enums.UserRole).DOCTOR.name())")
    public boolean deleteWeeklyAvailability(@InputArgument DayOfWeek dow) {
        return service.deleteWeeklyAvailability(getCurrentUserId(), dow);
    }
}
