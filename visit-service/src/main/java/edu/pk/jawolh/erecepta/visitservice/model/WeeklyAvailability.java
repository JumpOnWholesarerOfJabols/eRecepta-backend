package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class WeeklyAvailability {
    private final UUID doctorId;
    private final DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
