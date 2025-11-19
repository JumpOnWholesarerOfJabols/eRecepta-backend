package edu.pk.jawolh.erecepta.visitservice.mapper;

import com.example.demo.codegen.types.CreateWeeklyAvailabilityInput;
import edu.pk.jawolh.erecepta.visitservice.model.WeeklyAvailability;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class WeeklyAvailabilityInputMapper {
    public WeeklyAvailability mapFromInput(String doctorId, CreateWeeklyAvailabilityInput input) {
        WeeklyAvailability av = new WeeklyAvailability(UUID.fromString(doctorId), DayOfWeek.valueOf(input.getDayOfWeek().name()));
        av.setStartTime(LocalTime.parse(input.getStartTime()));
        av.setEndTime(LocalTime.parse(input.getEndTime()));

        return av;
    }
}
