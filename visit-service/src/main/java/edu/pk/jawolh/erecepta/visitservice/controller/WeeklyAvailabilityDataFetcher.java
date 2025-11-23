package edu.pk.jawolh.erecepta.visitservice.controller;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import edu.pk.jawolh.erecepta.visitservice.service.WeeklyAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class WeeklyAvailabilityDataFetcher extends AbstractDataFetcher {
    private final WeeklyAvailabilityService service;

    @DgsQuery
    public List<WeeklyAvailability> findAllWeeklyAvailabilities(@InputArgument UUID doctorId) {
        log.info("GraphQL Query: findAllWeeklyAvailabilities for doctor: {}", doctorId);
        return service.findAllByDoctorId(doctorId);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public boolean createWeeklyAvailability(@InputArgument CreateWeeklyAvailabilityInput waInput) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: createWeeklyAvailability by user: {}", userId);
        return service.createWeeklyAvailability(userId, waInput);
    }

    @DgsMutation
    @PreAuthorize("hasRole('DOCTOR')")
    public boolean deleteWeeklyAvailability(@InputArgument DayOfWeek dow) {
        UUID userId = getCurrentUserId();
        log.info("GraphQL Mutation: deleteWeeklyAvailability for day: {} by user: {}", dow, userId);
        return service.deleteWeeklyAvailability(userId, dow);
    }
}
