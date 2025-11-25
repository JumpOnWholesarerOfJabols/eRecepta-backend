package edu.pk.jawolh.erecepta.visitservice.mapper;

import com.example.demo.codegen.types.CreateAvailabilityExceptionInput;
import edu.pk.jawolh.erecepta.visitservice.model.AvailabilityException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class AvailabilityExceptionInputMapper {
    public AvailabilityException mapFromInput(UUID doctorId, CreateAvailabilityExceptionInput input) {
        AvailabilityException avex = new AvailabilityException(UUID.randomUUID(), doctorId);
        avex.setExceptionDate(LocalDate.parse(input.getExceptionDate()));
        avex.setStartTime(LocalTime.parse(input.getStartTime()));
        avex.setEndTime(LocalTime.parse(input.getEndTime()));

        return avex;
    }
}
