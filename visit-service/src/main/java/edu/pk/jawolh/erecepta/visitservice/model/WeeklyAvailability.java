package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class WeeklyAvailability {
    private final UUID doctorId;
    private final DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public void setStartTime(LocalTime startTime) {
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("startTime cannot be set after endTime!");
        }
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (startTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("endTime cannot be set before startTime!");
        }
        this.endTime = endTime;
    }
}
