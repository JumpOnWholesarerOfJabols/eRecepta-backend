package edu.pk.jawolh.erecepta.visitservice.model;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Visit {
    private final UUID id;
    private final UUID doctorId;
    private final UUID patientId;
    private final Specialization specialization;
    private LocalDateTime visitTime;
    private VisitStatus visitStatus;
}
