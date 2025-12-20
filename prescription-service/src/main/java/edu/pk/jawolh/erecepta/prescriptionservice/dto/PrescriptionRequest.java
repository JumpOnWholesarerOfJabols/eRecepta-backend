package edu.pk.jawolh.erecepta.prescriptionservice.dto;

import java.util.List;
import java.util.UUID;

public record PrescriptionRequest(
        UUID visitId,
        List<PrescribedMedicationRequest> medications
) {
}
