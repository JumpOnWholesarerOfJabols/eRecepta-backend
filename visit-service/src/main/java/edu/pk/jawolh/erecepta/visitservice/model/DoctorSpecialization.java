package edu.pk.jawolh.erecepta.visitservice.model;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;

import java.util.UUID;

public record DoctorSpecialization(UUID doctorId, Specialization specialization) {
}
