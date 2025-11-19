package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class AvailabilityException {
    private final UUID id;
    private final UUID doctorId;
    private LocalDate exceptionDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public void setExceptionDate(LocalDate exceptionDate) {
        if (LocalDate.now().isAfter(exceptionDate)) {
            throw new IllegalArgumentException("exceptionDate cannot be set in the past!");
        }
        this.exceptionDate = exceptionDate;
    }

    public void setStartTime(LocalTime startTime) {
        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("startTime cannot be set after endTime!");
        }
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("endTime cannot be set before startTime!");
        }
        this.endTime = endTime;
    }
}
