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
    @PreAuthorize("hasRole('DOCTOR')")
    public DayOfWeek createWeeklyAvailability(@InputArgument CreateWeeklyAvailabilityInput in) {
        return service.createWeeklyAvailability(getCurrentUserId(), in);
    }
}
