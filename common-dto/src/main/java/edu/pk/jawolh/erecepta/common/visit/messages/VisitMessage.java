package edu.pk.jawolh.erecepta.common.visit.messages;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitMessage(UUID patientId, UUID doctorId, LocalDateTime visitDateTime, Specialization specialization,
                           VisitStatus status) {
}
