package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AvailabilityException {
    private final UUID id;
    private final UUID doctorId;
    private LocalDate exceptionDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
