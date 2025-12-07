package edu.pk.jawolh.erecepta.visitservice.exception;

import edu.pk.jawolh.erecepta.common.visit.enums.Specialization;

import java.util.UUID;

public class DoctorSpecializationExistsException extends AbstractBadRequestException {
    private static final String MESSAGE = "Doctor %s has specialization %s already";

    public DoctorSpecializationExistsException(UUID doctorId, Specialization specialization) {
        super(MESSAGE.formatted(doctorId, specialization));
    }
}
