package edu.pk.jawolh.erecepta.visitservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Visit {
    private int id;
    private final String doctorId;
    private final String patientId;
    private final Specialization specialization;
    private LocalDateTime visitTime;
}
