package edu.pk.jawolh.erecepta.visitservice.exception;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;

import java.util.UUID;

public class DoctorSpecializationNotFoundException extends AbstractBadRequestException {
    private static final String MESSAGE = "Doctor %s does not have specialization %s";

    public DoctorSpecializationNotFoundException(UUID doctorId, Specialization specialization) {
        super(MESSAGE.formatted(doctorId, specialization));
    }
}
