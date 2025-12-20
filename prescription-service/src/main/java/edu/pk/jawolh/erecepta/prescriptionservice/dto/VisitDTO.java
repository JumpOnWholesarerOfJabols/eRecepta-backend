package edu.pk.jawolh.erecepta.prescriptionservice.dto;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitDTO(UUID id, UUID patientId, UUID doctorId, LocalDateTime visitTime, Specialization specialization,
                       VisitStatus visitStatus) {
}
