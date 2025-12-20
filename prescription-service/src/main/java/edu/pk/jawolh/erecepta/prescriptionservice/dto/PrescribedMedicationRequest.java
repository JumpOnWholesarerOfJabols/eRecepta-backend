package edu.pk.jawolh.erecepta.prescriptionservice.dto;

import java.util.UUID;

public record PrescribedMedicationRequest(
        UUID medicationId,
        String usageNotes
) {
}
