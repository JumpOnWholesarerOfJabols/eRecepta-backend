package edu.pk.jawolh.erecepta.common.visit.dtos;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;
import edu.pk.jawolh.erecepta.common.visit.enums.VisitStatus;

import java.time.LocalDateTime;

public record VisitDataDTO(LocalDateTime visitDateTime, Specialization specialization, VisitStatus status) {
}
