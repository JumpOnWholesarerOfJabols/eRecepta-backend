package edu.pk.jawolh.erecepta.visitservice.model;

import java.util.UUID;

public record DoctorSpecialization(UUID doctorId, Specialization specialization) {
}
